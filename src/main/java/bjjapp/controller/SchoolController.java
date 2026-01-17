package bjjapp.controller;

import bjjapp.entity.School;
import bjjapp.entity.SchoolOwner;
import bjjapp.entity.Invoice;
import bjjapp.dto.request.SchoolCreationRequest;
import bjjapp.dto.response.SchoolResponse;
import bjjapp.enums.SchoolStatus;
import bjjapp.service.SchoolService;
import bjjapp.service.InvoiceService;
import bjjapp.mapper.SchoolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller responsável pelos endpoints de gestão de Escolas (Super Admin).
 */
@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:5173", "http://localhost:3000",
        "https://appbjj.com.br", "https://appbjjfront-hvhk.vercel.app/" })
public class SchoolController {

    private final SchoolService schoolService;
    private final InvoiceService invoiceService;
    private final SchoolMapper schoolMapper;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll() {
        List<School> schools = schoolService.findAll();
        Map<String, Long> summary = schoolService.getSummary();
        Map<String, Object> response = Map.of(
                "summary", summary,
                "schools", schoolMapper.toResponseList(schools));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SchoolResponse> create(@RequestBody School school) {
        School created = schoolService.create(school);
        return ResponseEntity.ok(schoolMapper.toResponse(created));
    }

    @PostMapping("/with-owner")
    public ResponseEntity<SchoolResponse> createWithOwner(@Valid @RequestBody SchoolCreationRequest request) {
        School school = School.builder()
                .name(request.getSchoolName())
                .slug(request.getSchoolSlug())
                .phone(request.getSchoolPhone())
                .build();

        SchoolOwner owner = SchoolOwner.builder()
                .fullName(request.getOwnerFullName())
                .email(request.getOwnerEmail())
                .document(request.getOwnerDocument())
                .phone(request.getOwnerPhone())
                .build();

        School created = schoolService.createWithOwnerAndSubscription(
                school,
                owner,
                request.getSubscriptionAmount(),
                request.getTrialDays());
        return ResponseEntity.ok(schoolMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolResponse> update(@PathVariable Long id, @RequestBody School school) {
        School updated = schoolService.update(id, school);
        return ResponseEntity.ok(schoolMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<SchoolResponse> activate(@PathVariable Long id) {
        School activated = schoolService.activate(id);
        return ResponseEntity.ok(schoolMapper.toResponse(activated));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<SchoolResponse> deactivate(@PathVariable Long id) {
        School deactivated = schoolService.deactivate(id);
        return ResponseEntity.ok(schoolMapper.toResponse(deactivated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        schoolService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable Long id) {
        School school = schoolService.findById(id);
        Map<String, Object> status = Map.of(
                "id", school.getId(),
                "name", school.getName(),
                "slug", school.getSlug(),
                "status", school.getStatus(),
                "phone", school.getPhone() != null ? school.getPhone() : "",
                "trialStart", school.getCreatedAt(),
                "trialEnd", school.getTrialEndDate() != null ? school.getTrialEndDate() : "", // Handle potential null
                "isExpired", school.getTrialEndDate() != null && school.getTrialEndDate().isBefore(LocalDateTime.now()),
                "createdAt", school.getCreatedAt(),
                "updatedAt", school.getUpdatedAt() != null ? school.getUpdatedAt() : "");
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{id}/billing")
    public ResponseEntity<Map<String, Object>> getBilling(@PathVariable Long id) {
        Map<String, Object> billing = schoolService.getBillingInfo(id);
        // We could refactor getBillingInfo to return DTOs too, but it returns a
        // flexible Map.
        // For now, let's keep it but ideally we should map entities inside it.
        // Since it's a map, let's leave it as is or map manually if we want perfection.
        // The billing info contains: school, subscription, invoices.
        // Let's rely on default serialization for now or map it if it contains cycles.
        // School -> Subscription -> School (CYCLE!)
        // So we MUST map it.

        School school = (School) billing.get("school");
        bjjapp.entity.Subscription sub = (bjjapp.entity.Subscription) billing.get("subscription");
        List<Invoice> invoices = (List<Invoice>) billing.get("invoices");

        Map<String, Object> response = Map.of(
                "school", schoolMapper.toResponse(school),
                "subscription", schoolMapper.toSubscriptionResponse(sub),
                "invoices", invoices // Invoice might need DTO too or @JsonIgnoreProperties
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/invoices/generate")
    public ResponseEntity<Invoice> generateInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceService.generateNextInvoice(id);
        return ResponseEntity.ok(invoice);
    }
}

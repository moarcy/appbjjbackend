package bjjapp.controller;

import bjjapp.entity.School;
import bjjapp.entity.SchoolOwner;
import bjjapp.entity.Invoice;
import bjjapp.entity.School.SchoolStatus;
import bjjapp.service.SchoolService;
import bjjapp.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000", "https://appbjj.com.br", "https://appbjjfront-hvhk.vercel.app/"})
public class SchoolController {

    private final SchoolService schoolService;
    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll() {
        List<School> schools = schoolService.findAll();
        Map<String, Long> summary = schoolService.getSummary();
        Map<String, Object> response = Map.of(
            "summary", summary,
            "schools", schools
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<School> create(@RequestBody School school) {
        School created = schoolService.create(school);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/with-owner")
    public ResponseEntity<School> createWithOwner(@Valid @RequestBody SchoolCreationRequest request) {
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
            request.getTrialDays()
        );
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<School> update(@PathVariable Long id, @RequestBody School school) {
        School updated = schoolService.update(id, school);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<School> activate(@PathVariable Long id) {
        School activated = schoolService.activate(id);
        return ResponseEntity.ok(activated);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<School> deactivate(@PathVariable Long id) {
        School deactivated = schoolService.deactivate(id);
        return ResponseEntity.ok(deactivated);
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
            "phone", school.getPhone(),
            "trialStart", school.getCreatedAt(),
            "trialEnd", school.getCreatedAt().plusDays(30), // Exemplo: 30 dias trial
            "isExpired", school.getCreatedAt().plusDays(30).isBefore(LocalDateTime.now()),
            "createdAt", school.getCreatedAt(),
            "updatedAt", school.getUpdatedAt()
        );
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{id}/billing")
    public ResponseEntity<Map<String, Object>> getBilling(@PathVariable Long id) {
        Map<String, Object> billing = schoolService.getBillingInfo(id);
        return ResponseEntity.ok(billing);
    }

    @PostMapping("/{id}/invoices/generate")
    public ResponseEntity<Invoice> generateInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceService.generateNextInvoice(id);
        return ResponseEntity.ok(invoice);
    }
}

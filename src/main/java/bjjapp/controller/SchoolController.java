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
    private final bjjapp.mapper.InvoiceMapper invoiceMapper;

    @GetMapping
    public ResponseEntity<bjjapp.dto.response.SchoolListResponse> findAll() {
        return ResponseEntity.ok(bjjapp.dto.response.SchoolListResponse.builder()
                .schools(schoolMapper.toResponseList(schoolService.findAll()))
                .summary(schoolService.getSummary())
                .build());
    }

    @PostMapping
    public ResponseEntity<SchoolResponse> create(@RequestBody School school) {
        School created = schoolService.create(school);
        return ResponseEntity.ok(schoolMapper.toResponse(created));
    }

    @PostMapping("/with-owner")
    public ResponseEntity<SchoolResponse> createWithOwner(@Valid @RequestBody SchoolCreationRequest request) {
        School created = schoolService.createWithOwnerAndSubscription(
                School.builder()
                        .name(request.getSchoolName())
                        .slug(request.getSchoolSlug())
                        .phone(request.getSchoolPhone())
                        .build(),
                SchoolOwner.builder()
                        .fullName(request.getOwnerFullName())
                        .email(request.getOwnerEmail())
                        .document(request.getOwnerDocument())
                        .phone(request.getOwnerPhone())
                        .build(),
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
        return ResponseEntity.ok(schoolMapper.toResponse(schoolService.activate(id)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<SchoolResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(schoolMapper.toResponse(schoolService.deactivate(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        schoolService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/billing")
    public ResponseEntity<bjjapp.dto.response.SchoolBillingResponse> getBilling(@PathVariable Long id) {
        Map<String, Object> billing = schoolService.getBillingInfo(id);

        School school = (School) billing.get("school");
        bjjapp.entity.Subscription sub = (bjjapp.entity.Subscription) billing.get("subscription");
        List<Invoice> invoices = (List<Invoice>) billing.get("invoices");

        return ResponseEntity.ok(bjjapp.dto.response.SchoolBillingResponse.builder()
                .school(schoolMapper.toResponse(school))
                .subscription(schoolMapper.toSubscriptionResponse(sub))
                .invoices(invoiceMapper.toResponseList(invoices))
                .build());
    }

    @PostMapping("/{id}/invoices/generate")
    public ResponseEntity<bjjapp.dto.response.InvoiceResponse> generateInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceService.generateNextInvoice(id);
        return ResponseEntity.ok(invoiceMapper.toResponse(invoice));
    }
}

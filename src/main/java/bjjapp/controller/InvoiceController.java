package bjjapp.controller;

import bjjapp.entity.Invoice;
import bjjapp.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000", "https://appbjj.com.br", "https://appbjjfront-hvhk.vercel.app/"})
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/{id}/pay")
    public ResponseEntity<Invoice> markAsPaid(@PathVariable Long id, @RequestBody PayInvoiceRequest request) {
        Invoice paid = invoiceService.markAsPaid(id, request.getPaymentMethod(), request.getNotes());
        return ResponseEntity.ok(paid);
    }

    public static class PayInvoiceRequest {
        private Invoice.PaymentMethod paymentMethod;
        private String notes;

        // Getters and setters
        public Invoice.PaymentMethod getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(Invoice.PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}

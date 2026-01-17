package bjjapp.controller;

import bjjapp.dto.request.PayInvoiceRequest;
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
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:5173", "http://localhost:3000",
                "https://appbjj.com.br", "https://appbjjfront-hvhk.vercel.app/" })
public class InvoiceController {

        private final InvoiceService invoiceService;
        private final bjjapp.mapper.InvoiceMapper invoiceMapper;

        @GetMapping
        public ResponseEntity<List<bjjapp.dto.response.InvoiceResponse>> findAll() {
                return ResponseEntity.ok(invoiceMapper.toResponseList(invoiceService.findAll()));
        }

        @PostMapping("/{id}/pay")
        public ResponseEntity<bjjapp.dto.response.InvoiceResponse> markAsPaid(
                        @PathVariable Long id,
                        @RequestBody PayInvoiceRequest request) {
                Invoice paid = invoiceService.markAsPaid(
                                id,
                                request.getPaymentMethod(),
                                request.getNotes());
                return ResponseEntity.ok(invoiceMapper.toResponse(paid));
        }
}

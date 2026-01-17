package bjjapp.dto.request;

import bjjapp.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for marking an invoice as paid.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayInvoiceRequest {

    private PaymentMethod paymentMethod;
    
    private String notes;
}

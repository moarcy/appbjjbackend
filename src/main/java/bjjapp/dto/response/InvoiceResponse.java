package bjjapp.dto.response;

import bjjapp.enums.InvoiceStatus;
import bjjapp.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private Long schoolId;
    private String schoolName;
    private String referenceMonth; // Format: YYYY-MM
    private LocalDate dueDate;
    private BigDecimal amount;
    private InvoiceStatus status;
    private LocalDateTime paidAt;
    private PaymentMethod paymentMethod;
    private String notes;
}

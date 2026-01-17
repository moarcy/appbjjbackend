package bjjapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolBillingResponse {
    private SchoolResponse school;
    private SubscriptionResponse subscription;
    private List<InvoiceResponse> invoices;
}

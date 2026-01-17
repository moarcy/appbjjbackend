package bjjapp.dto.response;

import bjjapp.enums.BillingCycle;
import bjjapp.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private Long id;
    private BigDecimal amount;
    private BillingCycle billingCycle;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime trialEndDate;
    private LocalDateTime nextBillingDate;
    private LocalDateTime createdAt;
}

package bjjapp.dto.response;

import bjjapp.enums.SchoolStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolResponse {
    private Long id;
    private String name;
    private String slug;
    private SchoolStatus status;
    private String phone;
    private LocalDateTime trialEndDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SchoolOwnerResponse owner;
    private SubscriptionResponse subscription;
}

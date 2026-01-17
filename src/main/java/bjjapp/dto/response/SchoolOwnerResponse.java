package bjjapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolOwnerResponse {
    private Long id;
    private String fullName;
    private String email;
    private String document;
    private String phone;
    private LocalDateTime createdAt;
}

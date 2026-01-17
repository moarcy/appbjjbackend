package bjjapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new school with owner and subscription.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolCreationRequest {

    @NotBlank(message = "Nome da escola é obrigatório")
    private String schoolName;

    @NotBlank(message = "Slug da escola é obrigatório")
    private String schoolSlug;

    private String schoolPhone;

    @NotBlank(message = "Nome do responsável é obrigatório")
    private String ownerFullName;

    @NotBlank(message = "Email do responsável é obrigatório")
    private String ownerEmail;

    private String ownerDocument;

    private String ownerPhone;

    @NotNull(message = "Valor da mensalidade é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal subscriptionAmount;

    @Builder.Default
    private int trialDays = 30;
}

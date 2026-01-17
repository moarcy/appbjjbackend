package bjjapp.dto.request;

import bjjapp.enums.Faixa;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Faixa é obrigatória")
    private Faixa faixa;

    @Min(value = 0, message = "Grau mínimo é 0")
    @Max(value = 4, message = "Grau máximo é 4")
    private Integer grau;
}

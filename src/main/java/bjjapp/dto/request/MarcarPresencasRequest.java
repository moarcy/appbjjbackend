package bjjapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarcarPresencasRequest {
    @NotEmpty(message = "Lista de alunos não pode ser vazia")
    private Set<Long> alunosIds;
}

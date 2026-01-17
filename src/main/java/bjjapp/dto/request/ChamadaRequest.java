package bjjapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChamadaRequest {
    @NotNull(message = "Turma ID é obrigatório")
    private Long turmaId;

    @NotNull(message = "Professor ID é obrigatório")
    private Long professorId;
}

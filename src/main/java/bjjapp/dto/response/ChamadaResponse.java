package bjjapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChamadaResponse {
    private Long id;
    private TurmaResponse turma;
    private ProfessorResponse professor;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private Boolean finalizada;
    private Set<UserResponse> alunosPresentes;
    private int totalPresentes;
}

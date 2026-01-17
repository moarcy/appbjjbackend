package bjjapp.dto.request;

import bjjapp.enums.DiaSemana;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurmaRequest {
    private String nome;
    private String modalidade;
    private LocalTime horario;
    private Set<DiaSemana> dias;
}

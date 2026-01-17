package bjjapp.dto.response;

import bjjapp.enums.DiaSemana;
import bjjapp.enums.Modalidade;
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
public class TurmaResponse {
    private Long id;
    private String nome;
    private Modalidade modalidade;
    private LocalTime horario;
    private Set<DiaSemana> dias;
    private boolean ativo;
    private int studentCount;
}

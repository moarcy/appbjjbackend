package bjjapp.dto.response;

import bjjapp.enums.Faixa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorResponse {
    private Long id;
    private String nome;
    private Faixa faixa;
    private Integer grau;
    private boolean ativo;
}

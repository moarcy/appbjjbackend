package bjjapp.dto.response;

import bjjapp.enums.Faixa;
import bjjapp.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String nome;
    private Integer idade;
    private LocalDate dataNascimento;
    private Faixa faixa;
    private Integer grau;
    private String username;
    private Role role;
    private String nomeResponsavel;
    private String whatsappResponsavel;
    private String telefoneContato;
    private String dataInicioPratica;
    private String dataUltimaGraduacao;
    private Integer aulasAcumuladas;
    private Integer aulasDesdeUltimaGraduacao;
    private boolean ativo;
    private Set<TurmaResponse> turmas;
    private Set<Integer> criteriosConcluidos;
}

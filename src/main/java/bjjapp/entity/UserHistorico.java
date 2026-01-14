package bjjapp.entity;

import bjjapp.enums.TipoAlteracao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade para histórico de alterações do usuário
 */
@Entity
@Table(name = "user_historico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"turmas", "criteriosConcluidos"})
    private User user;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataHoraAlteracao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAlteracao tipoAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    // Método auxiliar para compatibilidade
    public LocalDateTime getDataHora() {
        return dataHoraAlteracao;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHoraAlteracao = dataHora;
    }

    // Factory methods

    public static UserHistorico criar(User user, String descricao, TipoAlteracao tipo) {
        return UserHistorico.builder()
            .user(user)
            .descricao(descricao)
            .dataHoraAlteracao(LocalDateTime.now())
            .tipoAlteracao(tipo)
            .build();
    }

    public static UserHistorico cadastro(User user) {
        return criar(user, "Usuário cadastrado", TipoAlteracao.CADASTRO);
    }

    public static UserHistorico presenca(User user, String turma) {
        return criar(user, "Presença registrada na turma: " + turma, TipoAlteracao.PRESENCA);
    }

    public static UserHistorico grau(User user, int grauAnterior, int grauNovo) {
        return criar(user, "Grau alterado de " + grauAnterior + " para " + grauNovo, TipoAlteracao.GRAU);
    }

    public static UserHistorico faixa(User user, String faixaAnterior, String faixaNova) {
        return criar(user, "Faixa alterada de " + faixaAnterior + " para " + faixaNova, TipoAlteracao.FAIXA);
    }
}

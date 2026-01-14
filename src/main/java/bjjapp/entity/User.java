package bjjapp.entity;

import bjjapp.enums.Faixa;
import bjjapp.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade User (anteriormente Aluno)
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Getter(AccessLevel.NONE)  // Usar método customizado getIdade()
    private Integer idade;

    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Faixa faixa = Faixa.BRANCA;

    @Min(value = 0, message = "Grau mínimo é 0")
    @Max(value = 4, message = "Grau máximo é 4")
    @Column(nullable = false)
    @Builder.Default
    private Integer grau = 0;

    // Campos de autenticação
    @Column(unique = true)
    private String username;
    private String password;
    @Transient
    private String plainPassword;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ALUNO;

    // Campos para responsáveis (menores de 18 anos)
    private String nomeResponsavel;
    private String whatsappResponsavel;

    // Campo para contato (maiores de 18 anos)
    private String telefoneContato;

    // Campos adicionais para todos os alunos
    private String dataInicioPratica; // Formato "YYYY-MM"
    private String dataUltimaGraduacao; // Formato "YYYY-MM"

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "aluno_turmas",
        joinColumns = @JoinColumn(name = "aluno_id"),
        inverseJoinColumns = @JoinColumn(name = "turma_id")
    )
    @JsonIgnoreProperties({"alunos"})
    @Builder.Default
    private Set<Turma> turmas = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Integer aulasAcumuladas = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer aulasDesdeUltimaGraduacao = 0;

    private LocalDate ultimaGraduacao;

    // Lista de critérios/técnicas concluídos (índices dos requisitos marcados)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "aluno_criterios", joinColumns = @JoinColumn(name = "aluno_id"))
    @Column(name = "criterio_index")
    @Builder.Default
    private Set<Integer> criteriosConcluidos = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    // Métodos de negócio

    /**
     * Retorna a idade do aluno.
     * Se idade for null mas dataNascimento existir, calcula a idade.
     */
    public Integer getIdade() {
        if (this.idade != null) {
            return this.idade;
        }
        if (this.dataNascimento != null) {
            return java.time.Period.between(this.dataNascimento, LocalDate.now()).getYears();
        }
        return null;
    }

    public void incrementarAula() {
        this.aulasAcumuladas++;
        this.aulasDesdeUltimaGraduacao++;
    }

    public void resetarAulasParaGraduacao() {
        this.aulasDesdeUltimaGraduacao = 0;
        this.ultimaGraduacao = LocalDate.now();
    }

    public void trocarFaixa(Faixa novaFaixa) {
        this.faixa = novaFaixa;
        this.grau = 0;
        this.aulasDesdeUltimaGraduacao = 0;
        this.aulasAcumuladas = 0;
        this.ultimaGraduacao = LocalDate.now();
    }

    public void concederGrau() {
        if (this.grau < 4) {
            this.grau++;
            resetarAulasParaGraduacao();
        }
        // Se já estiver no grau 4, não faz nada (professor pode decidir)
    }

    public int getAulasParaProximoGrau() {
        return switch (this.grau) {
            case 0 -> 20;
            case 1 -> 20;
            case 2 -> 30;
            case 3 -> 40;
            default -> 0;
        };
    }

    public int getAulasRestantes() {
        return Math.max(0, getAulasParaProximoGrau() - aulasDesdeUltimaGraduacao);
    }

    public boolean isAptoParaGraduacao() {
        return grau < 4 && aulasDesdeUltimaGraduacao >= getAulasParaProximoGrau();
    }

    /**
     * Verifica se o aluno é menor de idade (< 18 anos)
     */
    public boolean isMenorDeIdade() {
        Integer idade = getIdade();
        return idade != null && idade < 18;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}

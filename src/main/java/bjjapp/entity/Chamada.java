package bjjapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade Chamada (Presença)
 */
@Entity
@Table(name = "chamadas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chamada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "turma_id", nullable = false)
    @JsonIgnoreProperties({"alunos"})
    private Turma turma;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    private LocalDateTime dataHoraFim;

    @Column(nullable = false)
    @Builder.Default
    private Boolean finalizada = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "chamada_alunos",
        joinColumns = @JoinColumn(name = "chamada_id"),
        inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    @JsonIgnoreProperties({"turmas", "chamadas"})
    @Builder.Default
    private Set<User> alunosPresentes = new HashSet<>();

    // Métodos de negócio

    public void iniciar() {
        this.dataHoraInicio = LocalDateTime.now();
        this.finalizada = false;
    }

    public void finalizar() {
        if (this.finalizada) {
            throw new IllegalStateException("Chamada já está finalizada");
        }
        this.dataHoraFim = LocalDateTime.now();
        this.finalizada = true;
    }

    public void marcarPresenca(User aluno) {
        if (this.finalizada) {
            throw new IllegalStateException("Não é possível marcar presença em chamada finalizada");
        }
        this.alunosPresentes.add(aluno);
    }

    public void removerPresenca(User aluno) {
        if (this.finalizada) {
            throw new IllegalStateException("Não é possível alterar chamada finalizada");
        }
        this.alunosPresentes.remove(aluno);
    }

    public int getTotalPresentes() {
        return this.alunosPresentes.size();
    }
}

package bjjapp.entity;

import bjjapp.enums.DiaSemana;
import bjjapp.enums.Modalidade;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade Turma
 */
@Entity
@Table(name = "turmas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"modalidade", "horario"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Modalidade é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modalidade modalidade;

    @NotNull(message = "Horário é obrigatório")
    @Column(nullable = false)
    private LocalTime horario;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "turma_dias", joinColumns = @JoinColumn(name = "turma_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "dia")
    @Builder.Default
    private Set<DiaSemana> dias = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativa = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @ManyToMany(mappedBy = "turmas", fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"turmas", "chamadas"})
    @Builder.Default
    private Set<User> alunos = new HashSet<>();

    @NotNull(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;
}

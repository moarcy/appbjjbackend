package bjjapp.entity;

import bjjapp.enums.Faixa;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entidade Professor
 */
@Entity
@Table(name = "professores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "Faixa é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Faixa faixa;

    @Min(value = 0, message = "Grau mínimo é 0")
    @Max(value = 4, message = "Grau máximo é 4")
    @Column(nullable = false)
    @Builder.Default
    private Integer grau = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;
}

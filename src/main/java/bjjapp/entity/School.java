package bjjapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "schools", indexes = {
    @Index(name = "idx_school_slug", columnList = "slug"),
    @Index(name = "idx_school_status", columnList = "status")
})
@Where(clause = "deleted_at IS NULL")  // Filtro global para soft delete
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 100, updatable = false)  // Slug imutável
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SchoolStatus status;

    @Column(length = 20)
    private String phone;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum SchoolStatus {
        ACTIVE, INACTIVE
    }
}

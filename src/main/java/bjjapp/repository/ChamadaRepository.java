package bjjapp.repository;

import bjjapp.entity.Chamada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChamadaRepository extends JpaRepository<Chamada, Long> {

    List<Chamada> findByTurmaIdAndSchoolIdAndDeletedAtIsNull(Long turmaId, Long schoolId);

    List<Chamada> findByProfessorIdAndSchoolIdAndDeletedAtIsNull(Long professorId, Long schoolId);

    List<Chamada> findByFinalizadaAndSchoolIdAndDeletedAtIsNull(Boolean finalizada, Long schoolId);

    List<Chamada> findByFinalizadaFalseAndSchoolIdAndDeletedAtIsNull(Long schoolId);

    List<Chamada> findByFinalizadaTrueAndSchoolIdAndDeletedAtIsNull(Long schoolId);

    @Query("SELECT c FROM Chamada c WHERE c.dataHoraInicio BETWEEN :inicio AND :fim AND c.school.id = :schoolId AND c.deletedAt IS NULL")
    List<Chamada> findByPeriodoAndSchoolIdAndDeletedAtIsNull(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("schoolId") Long schoolId);

    @Query("SELECT c FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.school.id = :schoolId AND c.deletedAt IS NULL AND a.deletedAt IS NULL")
    List<Chamada> findByAlunoPresenteAndSchoolIdAndDeletedAtIsNull(@Param("alunoId") Long alunoId, @Param("schoolId") Long schoolId);

    @Query("SELECT COUNT(c) FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.school.id = :schoolId AND c.deletedAt IS NULL AND a.deletedAt IS NULL")
    Long countPresencasByAlunoIdAndSchoolIdAndDeletedAtIsNull(@Param("alunoId") Long alunoId, @Param("schoolId") Long schoolId);

    @Query("SELECT COUNT(c) FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.dataHoraFim > :desde AND c.school.id = :schoolId AND c.deletedAt IS NULL AND a.deletedAt IS NULL")
    Long countPresencasDesdeAndSchoolIdAndDeletedAtIsNull(@Param("alunoId") Long alunoId, @Param("desde") LocalDateTime desde, @Param("schoolId") Long schoolId);

    @Query("SELECT c FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.dataHoraInicio BETWEEN :inicio AND :fim AND c.school.id = :schoolId AND c.deletedAt IS NULL AND a.deletedAt IS NULL")
    List<Chamada> findByAlunoPresenteAndPeriodoAndSchoolIdAndDeletedAtIsNull(@Param("alunoId") Long alunoId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("schoolId") Long schoolId);

    @Query("SELECT c FROM Chamada c WHERE c.turma.id IN :turmasIds AND c.finalizada = true AND c.dataHoraInicio BETWEEN :inicio AND :fim AND c.school.id = :schoolId AND c.deletedAt IS NULL")
    List<Chamada> findByTurmasAndPeriodoAndSchoolIdAndDeletedAtIsNull(@Param("turmasIds") List<Long> turmasIds, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("schoolId") Long schoolId);

    List<Chamada> findAllBySchoolIdAndDeletedAtIsNull(Long schoolId);

    List<Chamada> findByTurmaIdAndSchoolIdAndDeletedAtIsNull(Long turmaId, Long schoolId);

    List<Chamada> findByProfessorIdAndSchoolIdAndDeletedAtIsNull(Long professorId, Long schoolId);

    List<Chamada> findByFinalizadaAndSchoolIdAndDeletedAtIsNull(Boolean finalizada, Long schoolId);

    List<Chamada> findByFinalizadaFalseAndSchoolIdAndDeletedAtIsNull(Long schoolId);

    List<Chamada> findByFinalizadaTrueAndSchoolIdAndDeletedAtIsNull(Long schoolId);

    @Query("SELECT c FROM Chamada c WHERE c.dataHoraInicio BETWEEN :inicio AND :fim AND c.school.id = :schoolId AND c.deletedAt IS NULL")
    List<Chamada> findByPeriodoAndSchoolIdAndDeletedAtIsNull(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("schoolId") Long schoolId);

    @Query("SELECT c FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.school.id = :schoolId AND c.deletedAt IS NULL AND a.deletedAt IS NULL")
    List<Chamada> findByAlunoPresenteAndSchoolIdAndDeletedAtIsNull(@Param("alunoId") Long alunoId, @Param("schoolId") Long schoolId);

    @Query("SELECT COUNT(c) FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.school.id = :schoolId AND c.deletedAt IS NULL AND a.deletedAt IS NULL")
    Long countPresencasByAlunoIdAndSchoolIdAndDeletedAtIsNull(@Param("alunoId") Long alunoId, @Param("schoolId") Long schoolId);

    @Query("SELECT COUNT(c) FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.dataHoraFim > :desde AND c.school.id = :schoolId AND c.deletedAt IS NULL AND a.deletedAt IS NULL")
    Long countPresencasDesdeAndSchoolIdAndDeletedAtIsNull(@Param("alunoId") Long alunoId, @Param("desde") LocalDateTime desde, @Param("schoolId") Long schoolId);

    @Query("SELECT c FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.dataHoraInicio BETWEEN :inicio AND :fim AND c.school.id = :schoolId AND c.deletedAt IS NULL AND a.deletedAt IS NULL")
    List<Chamada> findByAlunoPresenteAndPeriodoAndSchoolIdAndDeletedAtIsNull(@Param("alunoId") Long alunoId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("schoolId") Long schoolId);

    @Query("SELECT c FROM Chamada c WHERE c.turma.id IN :turmasIds AND c.finalizada = true AND c.dataHoraInicio BETWEEN :inicio AND :fim AND c.school.id = :schoolId AND c.deletedAt IS NULL")
    List<Chamada> findByTurmasAndPeriodoAndSchoolIdAndDeletedAtIsNull(@Param("turmasIds") List<Long> turmasIds, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("schoolId") Long schoolId);
}

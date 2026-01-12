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

    List<Chamada> findByTurmaId(Long turmaId);

    List<Chamada> findByProfessorId(Long professorId);

    List<Chamada> findByFinalizada(Boolean finalizada);

    List<Chamada> findByFinalizadaFalse();

    List<Chamada> findByFinalizadaTrue();

    @Query("SELECT c FROM Chamada c WHERE c.dataHoraInicio BETWEEN :inicio AND :fim")
    List<Chamada> findByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT c FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true")
    List<Chamada> findByAlunoPresente(@Param("alunoId") Long alunoId);

    @Query("SELECT COUNT(c) FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true")
    Long countPresencasByAlunoId(@Param("alunoId") Long alunoId);

    @Query("SELECT COUNT(c) FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.dataHoraFim > :desde")
    Long countPresencasDesde(@Param("alunoId") Long alunoId, @Param("desde") LocalDateTime desde);

    @Query("SELECT c FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.dataHoraInicio BETWEEN :inicio AND :fim")
    List<Chamada> findByAlunoPresenteAndPeriodo(@Param("alunoId") Long alunoId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT c FROM Chamada c WHERE c.turma.id IN :turmasIds AND c.finalizada = true AND c.dataHoraInicio BETWEEN :inicio AND :fim")
    List<Chamada> findByTurmasAndPeriodo(@Param("turmasIds") List<Long> turmasIds, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    List<Chamada> findAllByAtivoTrue();

    List<Chamada> findByTurmaIdAndAtivoTrue(Long turmaId);

    List<Chamada> findByProfessorIdAndAtivoTrue(Long professorId);

    List<Chamada> findByFinalizadaAndAtivoTrue(Boolean finalizada);

    List<Chamada> findByFinalizadaFalseAndAtivoTrue();

    List<Chamada> findByFinalizadaTrueAndAtivoTrue();

    @Query("SELECT c FROM Chamada c WHERE c.dataHoraInicio BETWEEN :inicio AND :fim AND c.ativo = true")
    List<Chamada> findByPeriodoAndAtivoTrue(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT c FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.ativo = true")
    List<Chamada> findByAlunoPresenteAndAtivoTrue(@Param("alunoId") Long alunoId);

    @Query("SELECT COUNT(c) FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.ativo = true")
    Long countPresencasByAlunoIdAndAtivoTrue(@Param("alunoId") Long alunoId);

    @Query("SELECT COUNT(c) FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.dataHoraFim > :desde AND c.ativo = true")
    Long countPresencasDesdeAndAtivoTrue(@Param("alunoId") Long alunoId, @Param("desde") LocalDateTime desde);

    @Query("SELECT c FROM Chamada c JOIN c.alunosPresentes a WHERE a.id = :alunoId AND c.finalizada = true AND c.dataHoraInicio BETWEEN :inicio AND :fim AND c.ativo = true")
    List<Chamada> findByAlunoPresenteAndPeriodoAndAtivoTrue(@Param("alunoId") Long alunoId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT c FROM Chamada c WHERE c.turma.id IN :turmasIds AND c.finalizada = true AND c.dataHoraInicio BETWEEN :inicio AND :fim AND c.ativo = true")
    List<Chamada> findByTurmasAndPeriodoAndAtivoTrue(@Param("turmasIds") List<Long> turmasIds, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}

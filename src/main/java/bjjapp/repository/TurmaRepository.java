package bjjapp.repository;

import bjjapp.entity.Turma;
import bjjapp.enums.Modalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    List<Turma> findByModalidade(Modalidade modalidade);

    List<Turma> findByAtiva(Boolean ativa);

    Optional<Turma> findByModalidadeAndHorario(Modalidade modalidade, LocalTime horario);

    List<Turma> findByAtivaTrue();

    List<Turma> findByModalidadeAndAtivaTrue(Modalidade modalidade);
}

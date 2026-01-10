package bjjapp.repository;

import bjjapp.entity.Professor;
import bjjapp.enums.Faixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    List<Professor> findByNomeContainingIgnoreCase(String nome);

    List<Professor> findByFaixa(Faixa faixa);

    List<Professor> findByFaixaAndGrauGreaterThanEqual(Faixa faixa, Integer grau);
}


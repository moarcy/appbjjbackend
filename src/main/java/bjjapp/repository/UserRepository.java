package bjjapp.repository;

import bjjapp.entity.User;
import bjjapp.enums.Faixa;
import bjjapp.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByAtivoTrue();

    List<User> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    List<User> findByFaixaAndAtivoTrue(Faixa faixa);

    List<User> findByFaixaAndGrauAndAtivoTrue(Faixa faixa, Integer grau);

    @Query("SELECT u FROM User u JOIN u.turmas t WHERE t.id = :turmaId AND u.ativo = true")
    List<User> findByTurmaIdAndAtivoTrue(@Param("turmaId") Long turmaId);

    List<User> findByRoleAndAtivoTrue(Role role);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}

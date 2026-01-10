package bjjapp.repository;

import bjjapp.entity.UserHistorico;
import bjjapp.entity.User;
import bjjapp.enums.TipoAlteracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserHistoricoRepository extends JpaRepository<UserHistorico, Long> {

    List<UserHistorico> findByUserIdOrderByDataHoraAlteracaoDesc(Long userId);

    List<UserHistorico> findByUserIdAndTipoAlteracao(Long userId, TipoAlteracao tipo);

    List<UserHistorico> findByDataHoraAlteracaoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<UserHistorico> findByTipoAlteracao(TipoAlteracao tipo);

    List<UserHistorico> findByUserOrderByDataHoraAlteracaoDesc(User user);
}

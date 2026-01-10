package bjjapp.service;

import bjjapp.entity.UserHistorico;
import bjjapp.entity.User;
import bjjapp.enums.TipoAlteracao;
import bjjapp.repository.UserHistoricoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserHistoricoService {

    private final UserHistoricoRepository historicoRepository;

    public UserHistorico save(UserHistorico historico) {
        return historicoRepository.save(historico);
    }

    public void registrarHistorico(User user, String tipoAlteracao, String descricao) {
        UserHistorico historico = UserHistorico.builder()
            .user(user)
            .tipoAlteracao(TipoAlteracao.valueOf(tipoAlteracao))
            .descricao(descricao)
            .dataHoraAlteracao(LocalDateTime.now())
            .build();
        save(historico);
    }

    public List<UserHistorico> getHistorico(User user) {
        return historicoRepository.findByUserOrderByDataHoraAlteracaoDesc(user);
    }

    @Transactional(readOnly = true)
    public List<UserHistorico> findByUserId(Long userId) {
        return historicoRepository.findByUserIdOrderByDataHoraAlteracaoDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<UserHistorico> findByUserIdAndTipo(Long userId, TipoAlteracao tipo) {
        return historicoRepository.findByUserIdAndTipoAlteracao(userId, tipo);
    }

    @Transactional(readOnly = true)
    public List<UserHistorico> findByTipo(TipoAlteracao tipo) {
        return historicoRepository.findByTipoAlteracao(tipo);
    }
}

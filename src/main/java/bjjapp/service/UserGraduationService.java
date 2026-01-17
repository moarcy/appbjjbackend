package bjjapp.service;

import bjjapp.entity.User;
import bjjapp.entity.UserHistorico;
import bjjapp.enums.Faixa;
import bjjapp.enums.TipoAlteracao;
import bjjapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGraduationService {

    private final UserRepository userRepository;
    private final UserHistoricoService historicoService;
    private final RequisitosGraduacaoService requisitosService; // Assuming this exists

    /**
     * Incrementa aula e verifica progresso
     */
    @Transactional
    public void registrarPresenca(User user) {
        user.incrementarAula();
        userRepository.save(user);
        // Pode checar se ficou apto e notificar...
    }

    @Transactional
    public User concederGrau(User user) {
        user.concederGrau();
        User saved = userRepository.save(user);
        historicoService.registrarHistorico(saved, TipoAlteracao.GRAU, "Grau concedido: " + saved.getGrau());
        return saved;
    }

    @Transactional
    public User trocarFaixa(User user, String novaFaixaName) {
        Faixa faixa = Faixa.valueOf(novaFaixaName.toUpperCase());
        user.trocarFaixa(faixa);
        User saved = userRepository.save(user);
        historicoService.registrarHistorico(saved, TipoAlteracao.FAIXA, "Faixa trocada para: " + saved.getFaixa());
        return saved;
    }

    @Transactional
    public User updateCriterios(User user, Set<Integer> criterios) {
        user.setCriteriosConcluidos(criterios);
        return userRepository.save(user);
    }

    public Map<String, Object> getStatus(User user) {
        int aulasParaProximoGrau = user.getAulasParaProximoGrau();
        int aulasRestantes = user.getAulasRestantes();
        boolean apto = user.isAptoParaGraduacao();

        return Map.of(
                "grauAtual", user.getGrau(),
                "aulasFaltando", aulasRestantes,
                "status", apto ? "Apto" : "Não apto",
                "aulasParaProximoGrau", aulasParaProximoGrau,
                "aulasDesdeUltimaGraduacao", user.getAulasDesdeUltimaGraduacao());
    }

    public double getPercentualProgressao(User user) {
        int aulasParaProximoGrau = user.getAulasParaProximoGrau();
        if (aulasParaProximoGrau == 0)
            return 100.0;
        return Math.min(100.0, (double) user.getAulasDesdeUltimaGraduacao() / aulasParaProximoGrau * 100);
    }

    public List<User> findAptosParaGraduacao() {
        return userRepository.findAllByAtivoTrue().stream()
                .filter(User::isAptoParaGraduacao)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getEstatisticasFaixas() {
        List<User> users = userRepository.findAllByAtivoTrue();
        return users.stream()
                .collect(Collectors.groupingBy(
                        user -> user.getFaixa().name(),
                        Collectors.counting()));
    }
}

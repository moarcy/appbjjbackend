package bjjapp.service;

import bjjapp.entity.User;
import bjjapp.entity.UserHistorico;
import bjjapp.entity.Turma;
import bjjapp.entity.UserPlainPassword;
import bjjapp.enums.Faixa;
import bjjapp.enums.Role;
import bjjapp.repository.UserRepository;
import bjjapp.repository.TurmaRepository;
import bjjapp.repository.UserPlainPasswordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final TurmaRepository turmaRepository;
    private final UserHistoricoService historicoService;
    private final RequisitosGraduacaoService requisitosService;
    private final PasswordEncoder passwordEncoder;
    private final UserPlainPasswordRepository userPlainPasswordRepository;

    public User save(User user) {
        if (user.getNome() == null || user.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        // Gerar credenciais automaticamente para novos usuários
        if (user.getId() == null && user.getUsername() == null) {
            String username = generateUsername(user.getNome());
            String rawPassword = generatePassword();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            // Role padrão é ALUNO, pode ser alterado para PROFESSOR via endpoint específico
        }

        User saved = userRepository.save(user);
        historicoService.registrarHistorico(saved, "CADASTRO", "Usuário cadastrado");
        return saved;
    }

    private String generateUsername(String nome) {
        String baseUsername = nome.toLowerCase().replace(" ", ".");
        String username = baseUsername;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }
        return username;
    }

    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public User save(User user, Set<Long> turmasIds) {
        if (turmasIds != null && !turmasIds.isEmpty()) {
            Set<Turma> turmas = turmasIds.stream()
                .map(turmaRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setTurmas(turmas);
        }
        return save(user);
    }

    public List<User> findAll() {
        return userRepository.findAllByAtivoTrue();
    }

    public User findById(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
    }

    public List<User> findByNome(String nome) {
        return userRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    public List<User> findByFaixa(Faixa faixa) {
        return userRepository.findByFaixaAndAtivoTrue(faixa);
    }

    public List<User> findByTurmaId(Long turmaId) {
        return userRepository.findByTurmaIdAndAtivoTrue(turmaId);
    }

    public List<User> findAptosParaGraduacao() {
        return userRepository.findAllByAtivoTrue().stream()
            .filter(User::isAptoParaGraduacao)
            .collect(Collectors.toList());
    }

    public User update(Long id, User user) {
        User existing = findById(id);
        existing.setNome(user.getNome());
        existing.setIdade(user.getIdade());
        existing.setDataNascimento(user.getDataNascimento());
        existing.setFaixa(user.getFaixa());
        existing.setGrau(user.getGrau());
        existing.setNomeResponsavel(user.getNomeResponsavel());
        existing.setWhatsappResponsavel(user.getWhatsappResponsavel());
        existing.setTelefoneContato(user.getTelefoneContato());
        existing.setDataInicioPratica(user.getDataInicioPratica());
        existing.setDataUltimaGraduacao(user.getDataUltimaGraduacao());
        existing.setTurmas(user.getTurmas());
        existing.setCriteriosConcluidos(user.getCriteriosConcluidos());

        User updated = userRepository.save(existing);
        historicoService.registrarHistorico(updated, "ATUALIZACAO", "Dados atualizados");
        return updated;
    }

    public void updateTurmas(Long userId, Set<Long> turmasIds) {
        User user = findById(userId);
        Set<Turma> turmas = turmasIds.stream()
            .map(turmaRepository::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
        user.setTurmas(turmas);
        userRepository.save(user);
        historicoService.registrarHistorico(user, "TURMA", "Turmas atualizadas");
    }

    public void delete(Long id) {
        User user = findById(id);
        user.setAtivo(false);
        userRepository.save(user);
        historicoService.registrarHistorico(user, "EXCLUSAO", "Usuário excluído");
    }

    public Map<String, Object> getStatus(Long id) {
        User user = findById(id);
        int aulasParaProximoGrau = user.getAulasParaProximoGrau();
        int aulasRestantes = user.getAulasRestantes();
        boolean apto = user.isAptoParaGraduacao();

        return Map.of(
            "grauAtual", user.getGrau(),
            "aulasFaltando", aulasRestantes,
            "status", apto ? "Apto" : "Não apto",
            "aulasParaProximoGrau", aulasParaProximoGrau,
            "aulasDesdeUltimaGraduacao", user.getAulasDesdeUltimaGraduacao()
        );
    }

    public double getPercentualProgressao(Long id) {
        User user = findById(id);
        int aulasParaProximoGrau = user.getAulasParaProximoGrau();
        if (aulasParaProximoGrau == 0) return 100.0;
        return Math.min(100.0, (double) user.getAulasDesdeUltimaGraduacao() / aulasParaProximoGrau * 100);
    }

    public User concederGrau(Long id) {
        User user = findById(id);
        user.concederGrau();
        User saved = userRepository.save(user);
        historicoService.registrarHistorico(saved, "GRAU", "Grau concedido: " + saved.getGrau());
        return saved;
    }

    public User trocarFaixa(Long id, String novaFaixa) {
        User user = findById(id);
        Faixa faixa = Faixa.valueOf(novaFaixa.toUpperCase());
        user.trocarFaixa(faixa);
        User saved = userRepository.save(user);
        historicoService.registrarHistorico(saved, "FAIXA", "Faixa trocada para: " + saved.getFaixa());
        return saved;
    }

    public List<UserHistorico> getHistorico(Long id) {
        User user = findById(id);
        return historicoService.getHistorico(user);
    }

    public Map<String, Long> getEstatisticasFaixas() {
        List<User> users = userRepository.findAllByAtivoTrue();
        return users.stream()
            .collect(Collectors.groupingBy(
                user -> user.getFaixa().name(),
                Collectors.counting()
            ));
    }

    public User updateCriterios(Long id, Set<Integer> criterios) {
        User user = findById(id);
        user.setCriteriosConcluidos(criterios);
        return userRepository.save(user);
    }

    // Métodos para autenticação
    public Optional<User> findByUsernameAuth(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRoleAndAtivoTrue(role);
    }

    public List<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(List::of).orElse(List.of());
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public User createProfessor(String nome) {
        User professor = User.builder()
            .nome(nome)
            .role(Role.PROFESSOR)
            .build();
        return save(professor);
    }
}

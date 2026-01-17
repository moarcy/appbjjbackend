package bjjapp.service;

import bjjapp.dto.response.UserCreationResponse;
import bjjapp.entity.Turma;
import bjjapp.entity.User;
import bjjapp.entity.UserHistorico;
import bjjapp.entity.UserPlainPassword;
import bjjapp.enums.Faixa;
import bjjapp.enums.Role;
import bjjapp.enums.TipoAlteracao;
import bjjapp.repository.TurmaRepository;
import bjjapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
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
    private final UserAuthService userAuthService;

    public User save(User user) {
        if (user.getNome() == null || user.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        // Delegar geração de credenciais para UserAuthService
        userAuthService.generateCredentialsForNewUser(user);

        User saved = userRepository.save(user);
        historicoService.registrarHistorico(saved, TipoAlteracao.CADASTRO, "Usuário cadastrado");
        return saved;
    }

    public User save(User user, Set<Long> turmasIds) {
        // 1. Salva o usuário sem turmas para garantir o ID
        Set<Turma> turmas = new HashSet<>();
        if (turmasIds != null && !turmasIds.isEmpty()) {
            turmas = turmasIds.stream()
                    .map(turmaRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
        }
        user.setTurmas(new HashSet<>());
        User saved = save(user);

        // 2. Associa o usuário às turmas (Bidirecional)
        for (Turma turma : turmas) {
            turma.getAlunos().add(saved);
            turmaRepository.save(turma);
        }
        saved.setTurmas(turmas);

        // 3. Salva novamente
        return userRepository.save(saved);
    }

    // Método Wrapper de conveniência para criação com retorno de senha
    public UserCreationResponse saveWithPlainPassword(User user, Set<Long> turmasIds) {
        boolean novoUsuario = user.getId() == null;

        // Salva (gera credenciais se novo)
        User saved = save(user, turmasIds);

        String username = saved.getUsername();
        String rawPassword = saved.getPlainPassword();

        // Se já existia e não tinha plain password setada no objeto, tenta recuperar
        if (!novoUsuario && rawPassword == null) {
            UserPlainPassword cred = userAuthService.getCredenciais(saved.getId());
            if (cred != null) {
                rawPassword = cred.getPlainPassword();
            } else {
                // Geração on-demand se não existir (legado)
                UserPlainPassword newCred = userAuthService.regenerateCredentials(saved);
                rawPassword = newCred.getPlainPassword();
            }
        }

        // Persistir plain password gerada (se houver)
        if (saved.getPlainPassword() != null) {
            userAuthService.savePlainPassword(saved, saved.getPlainPassword());
        }

        return new UserCreationResponse(saved, username, rawPassword);
    }

    /**
     * Retorna todos os usuários com papel de ALUNO ativos.
     * 
     * @return Lista de alunos ativos.
     */
    public List<User> findAllAlunos() {
        return userRepository.findAllByAtivoTrue().stream()
                .filter(user -> user.getRole() == Role.ALUNO)
                .collect(Collectors.toList());
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

    public User update(Long id, User user) {
        User existing = findById(id);
        updateUserFields(existing, user);

        // --- Bidirecionalidade Turmas ---
        Set<Turma> turmasAntigas = new HashSet<>(existing.getTurmas());
        Set<Turma> turmasNovas = user.getTurmas() != null ? user.getTurmas() : new HashSet<>();

        updateTurmasRelation(existing, turmasAntigas, turmasNovas);

        existing.setTurmas(turmasNovas);

        User updated = userRepository.save(existing);
        historicoService.registrarHistorico(updated, TipoAlteracao.ATUALIZACAO, "Dados atualizados");
        return updated;
    }

    public User update(Long id, User user, Set<Long> turmasIds) {
        User existing = findById(id);
        updateUserFields(existing, user);

        Set<Turma> turmasAntigas = new HashSet<>(existing.getTurmas());
        Set<Turma> turmasNovas = new HashSet<>();
        if (turmasIds != null && !turmasIds.isEmpty()) {
            turmasNovas = turmasIds.stream()
                    .map(turmaRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
        }

        updateTurmasRelation(existing, turmasAntigas, turmasNovas);
        existing.setTurmas(turmasNovas);

        User updated = userRepository.save(existing);
        historicoService.registrarHistorico(updated, TipoAlteracao.ATUALIZACAO, "Dados atualizados");
        return updated;
    }

    private void updateUserFields(User existing, User source) {
        existing.setNome(source.getNome());
        existing.setIdade(source.getIdade()); // set null effectively if not provided? Warning: getIdade is getter. Need
                                              // setter or logic.
        // Lombok Data provides setIdade if field exists. User field is "idade".
        // Wait, User entity has @Getter(AccessLevel.NONE) for idade, but setter?
        // It has @Data so it has SetIdade? Yes.

        existing.setDataNascimento(source.getDataNascimento());
        existing.setFaixa(source.getFaixa());
        existing.setGrau(source.getGrau());
        existing.setNomeResponsavel(source.getNomeResponsavel());
        existing.setWhatsappResponsavel(source.getWhatsappResponsavel());
        existing.setTelefoneContato(source.getTelefoneContato());
        existing.setDataInicioPratica(source.getDataInicioPratica());
        existing.setDataUltimaGraduacao(source.getDataUltimaGraduacao());
        existing.setCriteriosConcluidos(source.getCriteriosConcluidos());
    }

    private void updateTurmasRelation(User user, Set<Turma> turmasAntigas, Set<Turma> turmasNovas) {
        // Remover turmas antigas
        for (Turma turma : turmasAntigas) {
            if (!turmasNovas.contains(turma)) {
                turma.getAlunos().remove(user);
                turmaRepository.save(turma);
            }
        }
        // Adicionar novas
        for (Turma turma : turmasNovas) {
            turma.getAlunos().add(user);
            turmaRepository.save(turma);
        }
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
        historicoService.registrarHistorico(user, TipoAlteracao.TURMA, "Turmas atualizadas");
    }

    public void delete(Long id) {
        User user = findById(id);
        historicoService.registrarHistorico(user, TipoAlteracao.DESATIVACAO, "Usuário desativado");
        user.setAtivo(false);
        userRepository.save(user);
    }

    public List<UserHistorico> getHistorico(Long id) {
        User user = findById(id);
        return historicoService.getHistorico(user);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRoleAndAtivoTrue(role);
    }

    public List<User> findByUsername(String username) {
        return userRepository.findByUsername(username).stream().toList();
    }

    public User createProfessor(String nome) {
        User professor = User.builder()
                .nome(nome)
                .role(Role.PROFESSOR)
                .build();
        return save(professor);
    }
}

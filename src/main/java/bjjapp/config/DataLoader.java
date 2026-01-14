package bjjapp.config;

import bjjapp.entity.Professor;
import bjjapp.entity.Turma;
import bjjapp.entity.User;
import bjjapp.enums.DiaSemana;
import bjjapp.enums.Faixa;
import bjjapp.enums.Modalidade;
import bjjapp.enums.Role;
import bjjapp.repository.ProfessorRepository;
import bjjapp.repository.TurmaRepository;
import bjjapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

/**
 * Inicializa dados de exemplo no banco de dados
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Verificando dados iniciais...");

        // Criar professores se não existirem
        if (professorRepository.count() == 0) {
            log.info("Criando professores de exemplo...");

            professorRepository.save(Professor.builder()
                .nome("Mestre Carlos Silva")
                .faixa(Faixa.PRETA)
                .grau(4)
                .build());

            professorRepository.save(Professor.builder()
                .nome("Professor João Santos")
                .faixa(Faixa.PRETA)
                .grau(2)
                .build());

            professorRepository.save(Professor.builder()
                .nome("Professor Pedro Lima")
                .faixa(Faixa.MARROM)
                .grau(3)
                .build());

            log.info("3 professores criados");
        }

        // Criar turmas se não existirem
        if (turmaRepository.count() == 0) {
            log.info("Criando turmas de exemplo...");

            turmaRepository.save(Turma.builder()
                .nome("Turma Gi Manhã")
                .modalidade(Modalidade.GI)
                .horario(LocalTime.of(7, 0))
                .ativo(true)
                .dias(Set.of(DiaSemana.SEGUNDA, DiaSemana.QUARTA, DiaSemana.SEXTA))
                .build());

            turmaRepository.save(Turma.builder()
                .nome("Turma Gi Noite")
                .modalidade(Modalidade.GI)
                .horario(LocalTime.of(19, 0))
                .ativo(true)
                .dias(Set.of(DiaSemana.SEGUNDA, DiaSemana.QUARTA, DiaSemana.SEXTA))
                .build());

            turmaRepository.save(Turma.builder()
                .nome("Turma No-Gi")
                .modalidade(Modalidade.NO_GI)
                .horario(LocalTime.of(20, 0))
                .ativo(true)
                .dias(Set.of(DiaSemana.TERCA, DiaSemana.QUINTA))
                .build());

            turmaRepository.save(Turma.builder()
                .nome("Turma Kids")
                .modalidade(Modalidade.KIDS)
                .horario(LocalTime.of(17, 0))
                .ativo(true)
                .dias(Set.of(DiaSemana.SABADO))
                .build());

            log.info("4 turmas criadas");
        }

        // Criar alunos se não existirem
        if (userRepository.count() == 0) {
            log.info("Criando alunos de exemplo...");

            userRepository.save(User.builder()
                .nome("Maria Silva")
                .faixa(Faixa.BRANCA)
                .grau(0)
                .idade(25)
                .aulasAcumuladas(15)
                .aulasDesdeUltimaGraduacao(15)
                .role(Role.ALUNO)
                .build());

            userRepository.save(User.builder()
                .nome("José Santos")
                .faixa(Faixa.AZUL)
                .grau(2)
                .idade(30)
                .aulasAcumuladas(80)
                .aulasDesdeUltimaGraduacao(25)
                .role(Role.ALUNO)
                .build());

            userRepository.save(User.builder()
                .nome("Ana Oliveira")
                .faixa(Faixa.ROXA)
                .grau(1)
                .idade(28)
                .aulasAcumuladas(150)
                .aulasDesdeUltimaGraduacao(10)
                .role(Role.ALUNO)
                .build());

            userRepository.save(User.builder()
                .nome("Carlos Ferreira")
                .faixa(Faixa.BRANCA)
                .grau(3)
                .idade(22)
                .aulasAcumuladas(55)
                .aulasDesdeUltimaGraduacao(35)
                .role(Role.ALUNO)
                .build());

            userRepository.save(User.builder()
                .nome("Fernanda Costa")
                .faixa(Faixa.MARROM)
                .grau(0)
                .idade(35)
                .aulasAcumuladas(200)
                .aulasDesdeUltimaGraduacao(5)
                .role(Role.ALUNO)
                .build());

            log.info("5 alunos criados");
        }

        // Criar ou reativar usuário admin
        Optional<User> adminExistente = userRepository.findByUsername("admin");
        if (adminExistente.isPresent()) {
            User admin = adminExistente.get();
            if (!admin.isAtivo()) {
                admin.setAtivo(true);
                userRepository.save(admin);
                log.info("Usuário admin reativado");
            } else {
                log.info("Usuário admin já existe e está ativo, pulando criação");
            }
        } else {
            log.info("Criando usuário admin...");
            User admin = User.builder()
                .nome("Administrador")
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ADMIN)
                .ativo(true)
                .build();
            userRepository.save(admin);
            log.info("Usuário admin criado com username: admin e senha: admin");
        }

        log.info("Dados iniciais verificados!");
        log.info("Professores: {}", professorRepository.count());
        log.info("Turmas: {}", turmaRepository.count());
        log.info("Alunos: {}", userRepository.count());
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
}

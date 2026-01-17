package bjjapp.config;

import bjjapp.entity.Professor;
import bjjapp.entity.Turma;
import bjjapp.entity.User;
import bjjapp.entity.School;
import bjjapp.entity.SchoolOwner;
import bjjapp.entity.Subscription;
import bjjapp.entity.Invoice;
import bjjapp.enums.BillingCycle;
import bjjapp.enums.DiaSemana;
import bjjapp.enums.Faixa;
import bjjapp.enums.InvoiceStatus;
import bjjapp.enums.Modalidade;
import bjjapp.enums.PaymentMethod;
import bjjapp.enums.Role;
import bjjapp.enums.SchoolStatus;
import bjjapp.enums.SubscriptionStatus;
import bjjapp.repository.ProfessorRepository;
import bjjapp.repository.TurmaRepository;
import bjjapp.repository.UserRepository;
import bjjapp.repository.SchoolRepository;
import bjjapp.repository.SchoolOwnerRepository;
import bjjapp.repository.SubscriptionRepository;
import bjjapp.repository.InvoiceRepository;
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
    private final SchoolRepository schoolRepository;
    private final SchoolOwnerRepository schoolOwnerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvoiceRepository invoiceRepository;

    @Override
    public void run(String... args) {
        log.info("Verificando dados iniciais...");

        // Criar escola padrão se não existir
        School defaultSchool = schoolRepository.findBySlugAndDeletedAtIsNull("escola-padrao").orElse(null);
        if (defaultSchool == null) {
            log.info("Criando escola padrão...");
            defaultSchool = School.builder()
                    .name("Escola Padrão de Jiu-Jitsu")
                    .slug("escola-padrao")
                    .status(SchoolStatus.ACTIVE)
                    .phone("11999999999")
                    .build();
            schoolRepository.save(defaultSchool);
            log.info("Escola padrão criada");
        } else {
            log.info("Escola padrão já existe, pulando criação");
        }

        // Criar professores se não existirem
        if (professorRepository.count() == 0) {
            log.info("Criando professores de exemplo...");

            professorRepository.save(Professor.builder()
                    .nome("Mestre Carlos Silva")
                    .faixa(Faixa.PRETA)
                    .grau(4)
                    .school(defaultSchool)
                    .build());

            professorRepository.save(Professor.builder()
                    .nome("Professor João Santos")
                    .faixa(Faixa.PRETA)
                    .grau(2)
                    .school(defaultSchool)
                    .build());

            professorRepository.save(Professor.builder()
                    .nome("Professor Pedro Lima")
                    .faixa(Faixa.MARROM)
                    .grau(3)
                    .school(defaultSchool)
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
                    .school(defaultSchool)
                    .build());

            turmaRepository.save(Turma.builder()
                    .nome("Turma Gi Noite")
                    .modalidade(Modalidade.GI)
                    .horario(LocalTime.of(19, 0))
                    .ativo(true)
                    .dias(Set.of(DiaSemana.SEGUNDA, DiaSemana.QUARTA, DiaSemana.SEXTA))
                    .school(defaultSchool)
                    .build());

            turmaRepository.save(Turma.builder()
                    .nome("Turma No-Gi")
                    .modalidade(Modalidade.NO_GI)
                    .horario(LocalTime.of(20, 0))
                    .ativo(true)
                    .dias(Set.of(DiaSemana.TERCA, DiaSemana.QUINTA))
                    .school(defaultSchool)
                    .build());

            turmaRepository.save(Turma.builder()
                    .nome("Turma Kids")
                    .modalidade(Modalidade.KIDS)
                    .horario(LocalTime.of(17, 0))
                    .ativo(true)
                    .dias(Set.of(DiaSemana.SABADO))
                    .school(defaultSchool)
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
                    .school(defaultSchool)
                    .build());

            userRepository.save(User.builder()
                    .nome("José Santos")
                    .faixa(Faixa.AZUL)
                    .grau(2)
                    .idade(30)
                    .aulasAcumuladas(80)
                    .aulasDesdeUltimaGraduacao(25)
                    .role(Role.ALUNO)
                    .school(defaultSchool)
                    .build());

            userRepository.save(User.builder()
                    .nome("Ana Oliveira")
                    .faixa(Faixa.ROXA)
                    .grau(1)
                    .idade(28)
                    .aulasAcumuladas(150)
                    .aulasDesdeUltimaGraduacao(10)
                    .role(Role.ALUNO)
                    .school(defaultSchool)
                    .build());

            userRepository.save(User.builder()
                    .nome("Carlos Ferreira")
                    .faixa(Faixa.BRANCA)
                    .grau(3)
                    .idade(22)
                    .aulasAcumuladas(55)
                    .aulasDesdeUltimaGraduacao(35)
                    .role(Role.ALUNO)
                    .school(defaultSchool)
                    .build());

            userRepository.save(User.builder()
                    .nome("Fernanda Costa")
                    .faixa(Faixa.MARROM)
                    .grau(0)
                    .idade(35)
                    .aulasAcumuladas(200)
                    .aulasDesdeUltimaGraduacao(5)
                    .role(Role.ALUNO)
                    .school(defaultSchool)
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
                    .role(Role.SUPER_ADMIN)
                    .ativo(true)
                    .build();
            userRepository.save(admin);
            log.info("Usuário admin criado com username: admin e senha: admin");
        }

        // Criar dados de exemplo para schools com diferentes status
        if (schoolRepository.count() < 5) {
            log.info("Criando dados de exemplo para schools...");

            // School in TRIAL
            SchoolOwner owner1 = schoolOwnerRepository.save(SchoolOwner.builder()
                    .fullName("João Silva")
                    .email("joao@email.com")
                    .document("12345678901")
                    .phone("11999999991")
                    .build());
            Subscription sub1 = subscriptionRepository.save(Subscription.builder()
                    .amount(new java.math.BigDecimal("149.90"))
                    .billingCycle(BillingCycle.MONTHLY)
                    .status(SubscriptionStatus.TRIAL)
                    .startDate(java.time.LocalDateTime.now().minusDays(10))
                    .trialEndDate(java.time.LocalDateTime.now().plusDays(20))
                    .build());
            School school1 = School.builder()
                    .name("Academia Gracie Barra Centro")
                    .slug("gracie-barra-centro")
                    .status(SchoolStatus.ACTIVE)
                    .phone("11999999992")
                    .trialEndDate(java.time.LocalDateTime.now().plusDays(20))
                    .owner(owner1)
                    .subscription(sub1)
                    .build();
            schoolRepository.save(school1);

            // School ACTIVE
            SchoolOwner owner2 = schoolOwnerRepository.save(SchoolOwner.builder()
                    .fullName("Maria Santos")
                    .email("maria@email.com")
                    .document("12345678902")
                    .phone("11999999993")
                    .build());
            Subscription sub2 = subscriptionRepository.save(Subscription.builder()
                    .amount(new java.math.BigDecimal("199.90"))
                    .billingCycle(BillingCycle.MONTHLY)
                    .status(SubscriptionStatus.ACTIVE)
                    .startDate(java.time.LocalDateTime.now().minusDays(60))
                    .nextBillingDate(java.time.LocalDateTime.now().plusDays(30))
                    .build());
            School school2 = School.builder()
                    .name("Instituto de Jiu-Jitsu")
                    .slug("instituto-jiu-jitsu")
                    .status(SchoolStatus.ACTIVE)
                    .phone("11999999994")
                    .trialEndDate(java.time.LocalDateTime.now().minusDays(30))
                    .owner(owner2)
                    .subscription(sub2)
                    .build();
            schoolRepository.save(school2);

            // School INACTIVE
            SchoolOwner owner3 = schoolOwnerRepository.save(SchoolOwner.builder()
                    .fullName("Pedro Lima")
                    .email("pedro@email.com")
                    .document("12345678903")
                    .phone("11999999995")
                    .build());
            Subscription sub3 = subscriptionRepository.save(Subscription.builder()
                    .amount(new java.math.BigDecimal("129.90"))
                    .billingCycle(BillingCycle.MONTHLY)
                    .status(SubscriptionStatus.CANCELED)
                    .startDate(java.time.LocalDateTime.now().minusDays(90))
                    .build());
            School school3 = School.builder()
                    .name("Centro de Treinamento BJJ")
                    .slug("centro-treinamento-bjj")
                    .status(SchoolStatus.INACTIVE)
                    .phone("11999999996")
                    .trialEndDate(java.time.LocalDateTime.now().minusDays(60))
                    .owner(owner3)
                    .subscription(sub3)
                    .build();
            schoolRepository.save(school3);

            // School PAST_DUE
            SchoolOwner owner4 = schoolOwnerRepository.save(SchoolOwner.builder()
                    .fullName("Ana Oliveira")
                    .email("ana@email.com")
                    .document("12345678904")
                    .phone("11999999997")
                    .build());
            Subscription sub4 = subscriptionRepository.save(Subscription.builder()
                    .amount(new java.math.BigDecimal("179.90"))
                    .billingCycle(BillingCycle.MONTHLY)
                    .status(SubscriptionStatus.PAST_DUE)
                    .startDate(java.time.LocalDateTime.now().minusDays(45))
                    .nextBillingDate(java.time.LocalDateTime.now().minusDays(15))
                    .build());
            School school4 = School.builder()
                    .name("Escola de Artes Marciais")
                    .slug("escola-artes-marciais")
                    .status(SchoolStatus.ACTIVE)
                    .phone("11999999998")
                    .trialEndDate(java.time.LocalDateTime.now().minusDays(30))
                    .owner(owner4)
                    .subscription(sub4)
                    .build();
            schoolRepository.save(school4);

            // Criar invoices para as schools
            // School1 (TRIAL) - 1 invoice pendente
            invoiceRepository.save(Invoice.builder()
                    .school(school1)
                    .subscription(sub1)
                    .referenceMonth(java.time.YearMonth.now())
                    .dueDate(java.time.LocalDate.now().plusDays(10))
                    .amount(sub1.getAmount())
                    .status(InvoiceStatus.PENDING)
                    .build());

            // School2 (ACTIVE) - 1 invoice paga, 1 pendente
            invoiceRepository.save(Invoice.builder()
                    .school(school2)
                    .subscription(sub2)
                    .referenceMonth(java.time.YearMonth.now().minusMonths(1))
                    .dueDate(java.time.LocalDate.now().minusDays(30))
                    .amount(sub2.getAmount())
                    .status(InvoiceStatus.PAID)
                    .paidAt(java.time.LocalDateTime.now().minusDays(25))
                    .paymentMethod(PaymentMethod.MANUAL)
                    .notes("Pagamento via PIX")
                    .build());
            invoiceRepository.save(Invoice.builder()
                    .school(school2)
                    .subscription(sub2)
                    .referenceMonth(java.time.YearMonth.now())
                    .dueDate(java.time.LocalDate.now().plusDays(10))
                    .amount(sub2.getAmount())
                    .status(InvoiceStatus.PENDING)
                    .build());

            // School3 (INACTIVE) - 1 invoice cancelada
            invoiceRepository.save(Invoice.builder()
                    .school(school3)
                    .subscription(sub3)
                    .referenceMonth(java.time.YearMonth.now().minusMonths(2))
                    .dueDate(java.time.LocalDate.now().minusDays(60))
                    .amount(sub3.getAmount())
                    .status(InvoiceStatus.CANCELED)
                    .notes("Assinatura cancelada")
                    .build());

            // School4 (PAST_DUE) - 1 invoice overdue
            invoiceRepository.save(Invoice.builder()
                    .school(school4)
                    .subscription(sub4)
                    .referenceMonth(java.time.YearMonth.now().minusMonths(1))
                    .dueDate(java.time.LocalDate.now().minusDays(15))
                    .amount(sub4.getAmount())
                    .status(InvoiceStatus.OVERDUE)
                    .build());

            log.info("Invoices de exemplo criadas");
            log.info("4 schools de exemplo criadas");
        }

        log.info("Dados iniciais verificados!");
        log.info("Schools: {}", schoolRepository.count());
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

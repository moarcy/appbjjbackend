package bjjapp.service;

import bjjapp.context.SchoolContext;
import bjjapp.entity.Chamada;
import bjjapp.entity.Professor;
import bjjapp.entity.Turma;
import bjjapp.entity.User;
import bjjapp.entity.UserHistorico;
import bjjapp.repository.ChamadaRepository;
import bjjapp.repository.ProfessorRepository;
import bjjapp.repository.TurmaRepository;
import bjjapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service responsável pela gestão de Chamadas (presenças e aulas).
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChamadaService {

    private final ChamadaRepository chamadaRepository;
    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final UserRepository userRepository;
    private final UserHistoricoService historicoService;

    public Chamada iniciar(Long turmaId, Long professorId) {
        Long schoolId = SchoolContext.get();
        Turma turma = turmaRepository.findByIdAndSchoolIdAndDeletedAtIsNull(turmaId, schoolId)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada ou de outra escola: " + turmaId));

        Professor professor = professorRepository.findByIdAndSchoolIdAndDeletedAtIsNull(professorId, schoolId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Professor não encontrado ou de outra escola: " + professorId));

        // Validação cruzada
        if (!turma.getSchool().equals(professor.getSchool())) {
            throw new IllegalStateException("Referência cruzada entre escolas");
        }

        Chamada chamada = Chamada.builder()
                .turma(turma)
                .professor(professor)
                .school(turma.getSchool())
                .dataHoraInicio(LocalDateTime.now())
                .finalizada(false)
                .alunosPresentes(new java.util.HashSet<>())
                .build();

        return chamadaRepository.save(chamada);
    }

    @Transactional(readOnly = true)
    public List<Chamada> findAll() {
        Long schoolId = SchoolContext.get();
        return chamadaRepository.findAllBySchoolIdAndDeletedAtIsNull(schoolId);
    }

    @Transactional(readOnly = true)
    public Chamada findById(Long id) {
        return chamadaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamada não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Chamada> findAbertas() {
        Long schoolId = SchoolContext.get();
        return chamadaRepository.findByFinalizadaFalseAndSchoolIdAndDeletedAtIsNull(schoolId);
    }

    @Transactional(readOnly = true)
    public List<Chamada> findByTurmaId(Long turmaId) {
        Long schoolId = SchoolContext.get();
        return chamadaRepository.findByTurmaIdAndSchoolIdAndDeletedAtIsNull(turmaId, schoolId);
    }

    @Transactional(readOnly = true)
    public List<Chamada> findByAlunoId(Long alunoId) {
        Long schoolId = SchoolContext.get();
        return chamadaRepository.findByAlunoPresenteAndSchoolIdAndDeletedAtIsNull(alunoId, schoolId);
    }

    @Transactional(readOnly = true)
    public List<Chamada> findByAlunoIdAndPeriodo(Long alunoId, LocalDateTime inicio, LocalDateTime fim) {
        Long schoolId = SchoolContext.get();
        return chamadaRepository.findByAlunoPresenteAndPeriodoAndSchoolIdAndDeletedAtIsNull(alunoId, inicio, fim,
                schoolId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPresencasEausenciasPorPeriodo(Long alunoId, LocalDateTime inicio, LocalDateTime fim) {
        // LOG: Parâmetros recebidos
        log.debug("getPresencasEausenciasPorPeriodo - alunoId: {}, inicio: {}, fim: {}", alunoId, inicio, fim);

        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado: " + alunoId));

        List<Long> turmasIds = aluno.getTurmas().stream()
                .map(Turma::getId)
                .toList();

        if (aluno.getDeletedAt() != null) {
            Long schoolId = SchoolContext.get();
            List<Chamada> todasChamadas = chamadaRepository
                    .findByTurmasAndPeriodoAndSchoolIdAndDeletedAtIsNull(turmasIds, inicio, fim, schoolId);
            return Map.of(
                    "presencas", new java.util.ArrayList<>(),
                    "ausencias", todasChamadas,
                    "totalChamadas", todasChamadas.size(),
                    "totalPresencas", 0,
                    "totalAusencias", todasChamadas.size(),
                    "percentualPresenca", 0.0);
        }

        log.debug("getPresencasEausenciasPorPeriodo - turmasIds: {}", turmasIds);

        Long schoolId = SchoolContext.get();
        List<Chamada> todasChamadas = chamadaRepository.findByTurmasAndPeriodoAndSchoolIdAndDeletedAtIsNull(turmasIds,
                inicio, fim, schoolId);
        log.debug("getPresencasEausenciasPorPeriodo - chamadas encontradas: {}", todasChamadas.size());

        List<Chamada> presencas = new java.util.ArrayList<>();
        List<Chamada> ausencias = new java.util.ArrayList<>();

        for (Chamada chamada : todasChamadas) {
            if (chamada.getAlunosPresentes().contains(aluno)) {
                presencas.add(chamada);
            } else {
                ausencias.add(chamada);
            }
        }

        return Map.of(
                "presencas", presencas,
                "ausencias", ausencias,
                "totalChamadas", todasChamadas.size(),
                "totalPresencas", presencas.size(),
                "totalAusencias", ausencias.size(),
                "percentualPresenca",
                todasChamadas.isEmpty() ? 0.0 : (double) presencas.size() / todasChamadas.size() * 100);
    }

    public Chamada marcarPresenca(Long chamadaId, Long alunoId) {
        Chamada chamada = findById(chamadaId);

        if (chamada.getFinalizada()) {
            throw new IllegalStateException("Não é possível marcar presença em chamada finalizada");
        }

        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado: " + alunoId));

        chamada.marcarPresenca(aluno);
        return chamadaRepository.save(chamada);
    }

    public Chamada marcarPresencas(Long chamadaId, Set<Long> alunosIds) {
        Chamada chamada = findById(chamadaId);

        if (chamada.getFinalizada()) {
            throw new IllegalStateException("Não é possível marcar presenças em chamada finalizada");
        }

        for (Long alunoId : alunosIds) {
            User aluno = userRepository.findById(alunoId)
                    .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado: " + alunoId));
            chamada.marcarPresenca(aluno);
        }

        return chamadaRepository.save(chamada);
    }

    public Chamada removerPresenca(Long chamadaId, Long alunoId) {
        Chamada chamada = findById(chamadaId);

        if (chamada.getFinalizada()) {
            throw new IllegalStateException("Não é possível remover presença de chamada finalizada");
        }

        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado: " + alunoId));

        chamada.removerPresenca(aluno);
        return chamadaRepository.save(chamada);
    }

    public Chamada finalizar(Long chamadaId) {
        Chamada chamada = findById(chamadaId);

        if (chamada.getFinalizada()) {
            throw new IllegalStateException("Chamada já está finalizada");
        }

        chamada.finalizar();

        // Incrementar aulas para todos os alunos presentes
        for (User aluno : chamada.getAlunosPresentes()) {
            aluno.incrementarAula();
            userRepository.save(aluno);

            // Registrar histórico
            historicoService.save(
                    UserHistorico.presenca(aluno, chamada.getTurma().getModalidade().getDescricao()));
        }

        return chamadaRepository.save(chamada);
    }

    public void delete(Long id) {
        Chamada chamada = findById(id);
        if (chamada.getFinalizada()) {
            throw new IllegalStateException("Não é possível deletar chamada finalizada");
        }
        chamada.setDeletedAt(LocalDateTime.now());
        chamadaRepository.save(chamada);
    }

    @Transactional(readOnly = true)
    public Long countPresencas(Long alunoId) {
        Long schoolId = SchoolContext.get();
        return chamadaRepository.countPresencasByAlunoIdAndSchoolIdAndDeletedAtIsNull(alunoId, schoolId);
    }

    @Transactional(readOnly = true)
    public Long countPresencasDesde(Long alunoId, LocalDateTime desde) {
        Long schoolId = SchoolContext.get();
        return chamadaRepository.countPresencasDesdeAndSchoolIdAndDeletedAtIsNull(alunoId, desde, schoolId);
    }
}

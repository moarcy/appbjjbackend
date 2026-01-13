package bjjapp.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ChamadaService {

    private final ChamadaRepository chamadaRepository;
    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final UserRepository userRepository;
    private final UserHistoricoService historicoService;

    public Chamada iniciar(Long turmaId, Long professorId) {
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada: " + turmaId));

        Professor professor = professorRepository.findById(professorId)
            .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado: " + professorId));

        Chamada chamada = Chamada.builder()
            .turma(turma)
            .professor(professor)
            .dataHoraInicio(LocalDateTime.now())
            .finalizada(false)
            .alunosPresentes(new java.util.HashSet<>())
            .build();

        return chamadaRepository.save(chamada);
    }

    @Transactional(readOnly = true)
    public List<Chamada> findAll() {
        return chamadaRepository.findAllByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Chamada findById(Long id) {
        return chamadaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Chamada não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Chamada> findAbertas() {
        return chamadaRepository.findByFinalizadaFalseAndAtivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Chamada> findByTurmaId(Long turmaId) {
        return chamadaRepository.findByTurmaIdAndAtivoTrue(turmaId);
    }

    @Transactional(readOnly = true)
    public List<Chamada> findByAlunoId(Long alunoId) {
        return chamadaRepository.findByAlunoPresenteAndAtivoTrue(alunoId);
    }

    @Transactional(readOnly = true)
    public List<Chamada> findByAlunoIdAndPeriodo(Long alunoId, LocalDateTime inicio, LocalDateTime fim) {
        return chamadaRepository.findByAlunoPresenteAndPeriodoAndAtivoTrue(alunoId, inicio, fim);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPresencasEausenciasPorPeriodo(Long alunoId, LocalDateTime inicio, LocalDateTime fim) {
        // LOG: Parâmetros recebidos
        System.out.println("[DEBUG] getPresencasEausenciasPorPeriodo - alunoId: " + alunoId);
        System.out.println("[DEBUG] getPresencasEausenciasPorPeriodo - inicio: " + inicio);
        System.out.println("[DEBUG] getPresencasEausenciasPorPeriodo - fim: " + fim);

        User aluno = userRepository.findById(alunoId)
            .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado: " + alunoId));

        List<Long> turmasIds = aluno.getTurmas().stream()
            .map(Turma::getId)
            .toList();
        System.out.println("[DEBUG] getPresencasEausenciasPorPeriodo - turmasIds: " + turmasIds);

        List<Chamada> todasChamadas = chamadaRepository.findByTurmasAndPeriodoAndAtivoTrue(turmasIds, inicio, fim);
        System.out.println("[DEBUG] getPresencasEausenciasPorPeriodo - chamadas encontradas: " + todasChamadas.size());
        for (Chamada chamada : todasChamadas) {
            System.out.println("[DEBUG] chamadaId: " + chamada.getId() + ", dataHoraInicio: " + chamada.getDataHoraInicio() + ", finalizada: " + chamada.getFinalizada() + ", ativo: " + chamada.isAtivo());
        }

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
            "percentualPresenca", todasChamadas.isEmpty() ? 0.0 : (double) presencas.size() / todasChamadas.size() * 100
        );
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
                UserHistorico.presenca(aluno, chamada.getTurma().getModalidade().getDescricao())
            );
        }

        return chamadaRepository.save(chamada);
    }

    public void delete(Long id) {
        Chamada chamada = findById(id);
        if (chamada.getFinalizada()) {
            throw new IllegalStateException("Não é possível deletar chamada finalizada");
        }
        chamada.setAtivo(false);
        chamadaRepository.save(chamada);
    }

    @Transactional(readOnly = true)
    public Long countPresencas(Long alunoId) {
        return chamadaRepository.countPresencasByAlunoIdAndAtivoTrue(alunoId);
    }

    @Transactional(readOnly = true)
    public Long countPresencasDesde(Long alunoId, LocalDateTime desde) {
        return chamadaRepository.countPresencasDesdeAndAtivoTrue(alunoId, desde);
    }
}

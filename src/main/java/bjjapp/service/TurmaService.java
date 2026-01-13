package bjjapp.service;

import bjjapp.entity.Turma;
import bjjapp.enums.DiaSemana;
import bjjapp.enums.Modalidade;
import bjjapp.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class TurmaService {

    private final TurmaRepository turmaRepository;

    public Turma save(Turma turma, Set<DiaSemana> dias) {
        // Verificar duplicata considerando apenas turmas ativas e ignorando a própria turma (em edição)
        turmaRepository.findByModalidadeAndHorario(turma.getModalidade(), turma.getHorario())
            .ifPresent(t -> {
                if (t.isAtivo() && (turma.getId() == null || !t.getId().equals(turma.getId()))) {
                    throw new IllegalArgumentException("Já existe turma com essa modalidade e horário");
                }
            });

        if (dias != null && !dias.isEmpty()) {
            turma.setDias(dias);
        }

        return turmaRepository.save(turma);
    }

    public Turma save(Turma turma) {
        return save(turma, turma.getDias());
    }

    @Transactional(readOnly = true)
    public List<Turma> findAll() {
        return turmaRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Turma findById(Long id) {
        return turmaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Turma> findByModalidade(Modalidade modalidade) {
        return turmaRepository.findByModalidadeAndAtivoTrue(modalidade);
    }

    @Transactional(readOnly = true)
    public List<Turma> findAtivas() {
        return turmaRepository.findByAtivoTrue();
    }

    public Turma update(Long id, Turma turmaAtualizada, Set<DiaSemana> dias) {
        Turma turma = findById(id);
        turma.setNome(turmaAtualizada.getNome());
        turma.setModalidade(turmaAtualizada.getModalidade());
        turma.setHorario(turmaAtualizada.getHorario());
        turma.setAtivo(turmaAtualizada.isAtivo());

        if (dias != null) {
            turma.getDias().clear();
            turma.getDias().addAll(dias);
        }

        return turmaRepository.save(turma);
    }

    public Turma ativar(Long id) {
        Turma turma = findById(id);
        turma.setAtivo(true);
        return turmaRepository.save(turma);
    }

    public Turma desativar(Long id) {
        Turma turma = findById(id);
        turma.setAtivo(false);
        return turmaRepository.save(turma);
    }

    public void delete(Long id) {
        Turma turma = findById(id);
        turma.setAtivo(false);
        turmaRepository.save(turma);
    }
}

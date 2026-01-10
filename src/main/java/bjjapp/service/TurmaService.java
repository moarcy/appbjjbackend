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
        // Verificar duplicata
        turmaRepository.findByModalidadeAndHorario(turma.getModalidade(), turma.getHorario())
            .ifPresent(t -> {
                throw new IllegalArgumentException("Já existe turma com essa modalidade e horário");
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
        return turmaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Turma findById(Long id) {
        return turmaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Turma> findByModalidade(Modalidade modalidade) {
        return turmaRepository.findByModalidade(modalidade);
    }

    @Transactional(readOnly = true)
    public List<Turma> findAtivas() {
        return turmaRepository.findByAtivaTrue();
    }

    public Turma update(Long id, Turma turmaAtualizada, Set<DiaSemana> dias) {
        Turma turma = findById(id);
        turma.setModalidade(turmaAtualizada.getModalidade());
        turma.setHorario(turmaAtualizada.getHorario());
        turma.setAtiva(turmaAtualizada.getAtiva());

        if (dias != null) {
            turma.getDias().clear();
            turma.getDias().addAll(dias);
        }

        return turmaRepository.save(turma);
    }

    public Turma ativar(Long id) {
        Turma turma = findById(id);
        turma.setAtiva(true);
        return turmaRepository.save(turma);
    }

    public Turma desativar(Long id) {
        Turma turma = findById(id);
        turma.setAtiva(false);
        return turmaRepository.save(turma);
    }

    public void delete(Long id) {
        if (!turmaRepository.existsById(id)) {
            throw new IllegalArgumentException("Turma não encontrada: " + id);
        }
        turmaRepository.deleteById(id);
    }
}


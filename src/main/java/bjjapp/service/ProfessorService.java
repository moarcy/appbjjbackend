package bjjapp.service;

import bjjapp.entity.Professor;
import bjjapp.enums.Faixa;
import bjjapp.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public Professor save(Professor professor) {
        validarGrau(professor.getGrau());
        return professorRepository.save(professor);
    }

    @Transactional(readOnly = true)
    public List<Professor> findAll() {
        return professorRepository.findAllByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Professor findById(Long id) {
        return professorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Professor> findByFaixa(Faixa faixa) {
        return professorRepository.findByFaixaAndAtivoTrue(faixa);
    }

    @Transactional(readOnly = true)
    public List<Professor> findByNome(String nome) {
        return professorRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    public Professor update(Long id, Professor professorAtualizado) {
        Professor professor = findById(id);
        professor.setNome(professorAtualizado.getNome());
        professor.setFaixa(professorAtualizado.getFaixa());
        validarGrau(professorAtualizado.getGrau());
        professor.setGrau(professorAtualizado.getGrau());
        return professorRepository.save(professor);
    }

    public void delete(Long id) {
        Professor professor = findById(id);
        professor.setAtivo(false);
        professorRepository.save(professor);
    }

    private void validarGrau(Integer grau) {
        if (grau != null && (grau < 0 || grau > 4)) {
            throw new IllegalArgumentException("Grau deve ser entre 0 e 4");
        }
    }
}

package bjjapp.controller;

import bjjapp.dto.request.ProfessorRequest;
import bjjapp.dto.response.ProfessorResponse;
import bjjapp.entity.Professor;
import bjjapp.enums.Faixa;
import bjjapp.mapper.ProfessorMapper;
import bjjapp.service.ProfessorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professores")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:5173", "http://localhost:3000",
        "https://appbjj.com.br" })
public class ProfessorController {

    private final ProfessorService professorService;
    private final ProfessorMapper professorMapper;

    @PostMapping("/save")
    public ResponseEntity<ProfessorResponse> save(@Valid @RequestBody ProfessorRequest request) {
        Professor professor = professorMapper.toEntity(request);
        Professor saved = professorService.save(professor);
        return ResponseEntity.ok(professorMapper.toResponse(saved));
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<ProfessorResponse>> findAll() {
        List<Professor> professors = professorService.findAll();
        return ResponseEntity.ok(professorMapper.toResponseList(professors));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            Professor professor = professorService.findById(id);
            return ResponseEntity.ok(professorMapper.toResponse(professor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/findByFaixa")
    public ResponseEntity<List<ProfessorResponse>> findByFaixa(@RequestParam Faixa faixa) {
        List<Professor> professors = professorService.findByFaixa(faixa);
        return ResponseEntity.ok(professorMapper.toResponseList(professors));
    }

    @GetMapping("/findByNome")
    public ResponseEntity<List<ProfessorResponse>> findByNome(@RequestParam String nome) {
        List<Professor> professors = professorService.findByNome(nome);
        return ResponseEntity.ok(professorMapper.toResponseList(professors));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProfessorRequest request) {
        try {
            // Note: In a deeper refactoring, we'd use updateEntityFromRequest combined with
            // service
            // but for now we map to entity so service can merge.
            // Better yet: Service should take DTO or we fetch -> map -> save.
            // Following current pattern:
            Professor professor = professorMapper.toEntity(request);
            Professor updated = professorService.update(id, professor);
            return ResponseEntity.ok(professorMapper.toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            Professor desativado = professorService.desativar(id);
            return ResponseEntity.ok(professorMapper.toResponse(desativado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

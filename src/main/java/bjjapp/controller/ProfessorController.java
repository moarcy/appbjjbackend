package bjjapp.controller;

import bjjapp.entity.Professor;
import bjjapp.enums.Faixa;
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
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000", "https://appbjj.com.br"})
public class ProfessorController {

    private final ProfessorService professorService;

    @PostMapping("/save")
    public ResponseEntity<Professor> save(@Valid @RequestBody Professor professor) {
        return ResponseEntity.ok(professorService.save(professor));
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Professor>> findAll() {
        return ResponseEntity.ok(professorService.findAll());
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(professorService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/findByFaixa")
    public ResponseEntity<List<Professor>> findByFaixa(@RequestParam Faixa faixa) {
        return ResponseEntity.ok(professorService.findByFaixa(faixa));
    }

    @GetMapping("/findByNome")
    public ResponseEntity<List<Professor>> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(professorService.findByNome(nome));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Professor professor) {
        try {
            return ResponseEntity.ok(professorService.update(id, professor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            professorService.delete(id);
            return ResponseEntity.ok("Professor desativado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

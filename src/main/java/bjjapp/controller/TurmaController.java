package bjjapp.controller;

import bjjapp.entity.Turma;
import bjjapp.enums.DiaSemana;
import bjjapp.enums.Modalidade;
import bjjapp.service.TurmaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/turmas")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000"})
public class TurmaController {

    private final TurmaService turmaService;

    public record TurmaRequest(Turma turma, Set<DiaSemana> dias) {}

    @PostMapping
    public ResponseEntity<?> save(@RequestBody TurmaRequest request) {
        try {
            Turma salva = turmaService.save(request.turma(), request.dias());
            return ResponseEntity.ok(salva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Turma>> findAll() {
        return ResponseEntity.ok(turmaService.findAll());
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(turmaService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/findByModalidade")
    public ResponseEntity<List<Turma>> findByModalidade(@RequestParam Modalidade modalidade) {
        return ResponseEntity.ok(turmaService.findByModalidade(modalidade));
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<Turma>> findAtivas() {
        return ResponseEntity.ok(turmaService.findAtivas());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody TurmaRequest request) {
        try {
            return ResponseEntity.ok(turmaService.update(id, request.turma(), request.dias()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/ativar/{id}")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(turmaService.ativar(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/desativar/{id}")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(turmaService.desativar(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

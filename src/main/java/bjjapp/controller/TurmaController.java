package bjjapp.controller;

import bjjapp.entity.Turma;
import bjjapp.enums.DiaSemana;
import bjjapp.enums.Modalidade;
import bjjapp.service.TurmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/turmas")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000", "https://appbjj.com.br"})
public class TurmaController {

    private final TurmaService turmaService;

    public record TurmaRequest(String nome, String modalidade, LocalTime horario, Set<DiaSemana> dias) {}

    @PostMapping
    public ResponseEntity<?> save(@RequestBody TurmaRequest request) {
        try {
            Turma turma = new Turma();
            turma.setNome(request.nome());
            turma.setModalidade(Modalidade.fromDescricao(request.modalidade()));
            turma.setHorario(request.horario());
            // Garante que novas turmas sejam criadas como ativas, a não ser que explicitamente informado
            if (turma.getId() == null) {
                turma.setAtivo(true);
            }
            Turma salva = turmaService.save(turma, request.dias());
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
            Turma turma = new Turma();
            turma.setId(id);
            turma.setNome(request.nome());
            turma.setModalidade(Modalidade.fromDescricao(request.modalidade()));
            turma.setHorario(request.horario());
            return ResponseEntity.ok(turmaService.update(id, turma, request.dias()));
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

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(turmaService.desativar(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

package bjjapp.controller;

import bjjapp.entity.Turma;
import bjjapp.enums.Modalidade;
import bjjapp.service.TurmaService;
import bjjapp.dto.request.TurmaRequest;
import bjjapp.dto.response.TurmaResponse;
import bjjapp.mapper.TurmaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turmas")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:5173", "http://localhost:3000",
        "https://appbjj.com.br" })
public class TurmaController {

    private final TurmaService turmaService;
    private final TurmaMapper turmaMapper;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody TurmaRequest request) {
        try {
            // Note: Service expects (Turma, Set<DiaSemana>) but Mapper handles creation.
            // However, Service.save logic handles dias separately?
            // Looking at previous controller: turmaService.save(turma, request.dias())
            // So I should map to Entity and pass dias.

            Turma turma = turmaMapper.toEntity(request);
            // Default active if new (handled by Mapper constant but safe to double check or
            // trust service)

            Turma salva = turmaService.save(turma, request.getDias());
            return ResponseEntity.ok(turmaMapper.toResponse(salva));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<TurmaResponse>> findAll() {
        List<Turma> turmas = turmaService.findAll();
        return ResponseEntity.ok(turmaMapper.toResponseList(turmas));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            Turma turma = turmaService.findById(id);
            return ResponseEntity.ok(turmaMapper.toResponse(turma));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/findByModalidade")
    public ResponseEntity<List<TurmaResponse>> findByModalidade(@RequestParam Modalidade modalidade) {
        List<Turma> turmas = turmaService.findByModalidade(modalidade);
        return ResponseEntity.ok(turmaMapper.toResponseList(turmas));
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<TurmaResponse>> findAtivas() {
        List<Turma> turmas = turmaService.findAtivas();
        return ResponseEntity.ok(turmaMapper.toResponseList(turmas));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody TurmaRequest request) {
        try {
            // Service expects (id, turma, dias)
            // It seems service update logic re-sets fields from passed 'turma' object.
            Turma turmaUpdate = turmaMapper.toEntity(request);
            // Ensure ID is passed to service if needed, or service handles it via param.
            // Service method: update(Long id, Turma turma, Set<DiaSemana> dias)

            Turma updated = turmaService.update(id, turmaUpdate, request.getDias());
            return ResponseEntity.ok(turmaMapper.toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/ativar/{id}")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            Turma ativada = turmaService.ativar(id);
            return ResponseEntity.ok(turmaMapper.toResponse(ativada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            Turma desativada = turmaService.desativar(id);
            return ResponseEntity.ok(turmaMapper.toResponse(desativada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

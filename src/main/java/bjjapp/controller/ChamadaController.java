package bjjapp.controller;

import bjjapp.entity.Chamada;
import bjjapp.service.ChamadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/chamadas")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000"})
public class ChamadaController {

    private final ChamadaService chamadaService;

    public record IniciarChamadaRequest(Long turmaId, Long professorId) {}
    public record MarcarPresencasRequest(Set<Long> alunosIds) {}

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciar(@RequestBody IniciarChamadaRequest request) {
        try {
            if (request.turmaId() == null || request.professorId() == null) {
                return ResponseEntity.badRequest().body("turmaId e professorId são obrigatórios");
            }
            Chamada chamada = chamadaService.iniciar(request.turmaId(), request.professorId());
            return ResponseEntity.ok(chamada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Chamada>> findAll() {
        return ResponseEntity.ok(chamadaService.findAll());
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(chamadaService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/abertas")
    public ResponseEntity<List<Chamada>> findAbertas() {
        List<Chamada> abertas = chamadaService.findAbertas();
        if (abertas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(abertas);
    }

    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<Chamada>> findByTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(chamadaService.findByTurmaId(turmaId));
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<?> findByAluno(
            @PathVariable Long alunoId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            if (startDate != null && endDate != null) {
                // Parse robusto das datas
                java.time.LocalDate inicioData = java.time.LocalDate.parse(startDate);
                java.time.LocalDate fimData = java.time.LocalDate.parse(endDate);
                java.time.LocalDateTime inicio = inicioData.atStartOfDay();
                java.time.LocalDateTime fim = fimData.atTime(23, 59, 59);
                Map<String, Object> resultado = chamadaService.getPresencasEausenciasPorPeriodo(alunoId, inicio, fim);
                return ResponseEntity.ok(Map.of(
                    "presencas", resultado.get("presencas"),
                    "ausencias", resultado.get("ausencias"),
                    "totalChamadas", resultado.get("totalChamadas"),
                    "totalPresencas", resultado.get("totalPresencas"),
                    "totalAusencias", resultado.get("totalAusencias"),
                    "percentualPresenca", resultado.get("percentualPresenca"),
                    "periodoFiltrado", true,
                    "dataInicio", startDate,
                    "dataFim", endDate
                ));
            } else {
                List<Chamada> chamadas = chamadaService.findByAlunoId(alunoId);
                Long totalPresencas = chamadaService.countPresencas(alunoId);
                return ResponseEntity.ok(Map.of(
                    "chamadas", chamadas,
                    "totalPresencas", totalPresencas,
                    "periodoFiltrado", false
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar chamadas: " + e.getMessage());
        }
    }

    @GetMapping("/presencas-ausencias/{alunoId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<?> getPresencasAusencias(
            @PathVariable Long alunoId,
            @RequestParam(required = false, name = "startDate") String startDate,
            @RequestParam(required = false, name = "endDate") String endDate,
            @RequestParam(required = false, name = "inicio") String inicio,
            @RequestParam(required = false, name = "fim") String fim) {
        // Prioriza startDate/endDate, mas aceita inicio/fim
        String dataInicio = startDate != null ? startDate : inicio;
        String dataFim = endDate != null ? endDate : fim;
        return findByAluno(alunoId, dataInicio, dataFim);
    }

    @PostMapping("/{id}/presenca/{alunoId}")
    public ResponseEntity<?> marcarPresenca(@PathVariable Long id, @PathVariable Long alunoId) {
        try {
            return ResponseEntity.ok(chamadaService.marcarPresenca(id, alunoId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/presencas")
    public ResponseEntity<?> marcarPresencas(@PathVariable Long id, @RequestBody MarcarPresencasRequest request) {
        try {
            if (request.alunosIds() == null || request.alunosIds().isEmpty()) {
                return ResponseEntity.badRequest().body("alunosIds é obrigatório");
            }
            return ResponseEntity.ok(chamadaService.marcarPresencas(id, request.alunosIds()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/presenca/{alunoId}")
    public ResponseEntity<?> removerPresenca(@PathVariable Long id, @PathVariable Long alunoId) {
        try {
            return ResponseEntity.ok(chamadaService.removerPresenca(id, alunoId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(chamadaService.finalizar(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            chamadaService.delete(id); // Soft delete: apenas marca como inativo
            return ResponseEntity.ok("Chamada deletada com sucesso");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

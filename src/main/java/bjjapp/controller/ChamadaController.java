package bjjapp.controller;

import bjjapp.dto.request.ChamadaRequest;
import bjjapp.dto.request.MarcarPresencasRequest;
import bjjapp.dto.response.ChamadaResponse;
import bjjapp.entity.Chamada;
import bjjapp.mapper.ChamadaMapper;
import bjjapp.service.ChamadaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chamadas")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:5173", "http://localhost:3000" })
public class ChamadaController {

    private final ChamadaService chamadaService;
    private final ChamadaMapper chamadaMapper;

    @PostMapping("/iniciar")
    public ResponseEntity<ChamadaResponse> iniciar(@Valid @RequestBody ChamadaRequest request) {
        Chamada chamada = chamadaService.iniciar(request.getTurmaId(), request.getProfessorId());
        return ResponseEntity.ok(chamadaMapper.toResponse(chamada));
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<ChamadaResponse>> findAll() {
        List<Chamada> chamadas = chamadaService.findAll();
        return ResponseEntity.ok(chamadaMapper.toResponseList(chamadas));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            Chamada chamada = chamadaService.findById(id);
            return ResponseEntity.ok(chamadaMapper.toResponse(chamada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/abertas")
    public ResponseEntity<List<ChamadaResponse>> findAbertas() {
        List<Chamada> abertas = chamadaService.findAbertas();
        if (abertas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(chamadaMapper.toResponseList(abertas));
    }

    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<ChamadaResponse>> findByTurma(@PathVariable Long turmaId) {
        List<Chamada> chamadas = chamadaService.findByTurmaId(turmaId);
        return ResponseEntity.ok(chamadaMapper.toResponseList(chamadas));
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

                // Keep returning Map for complex stats, but ensure lists inside are DTOs if
                // possible?
                // The service returns a Map. Refactoring service logic is out of scope for this
                // precise step,
                // but we can trust the map structure or eventually DTO-ify it. For now, let's
                // keep it safe.

                return ResponseEntity.ok(Map.of(
                        "presencas", resultado.get("presencas"), // Likely list of objects, ideally should map
                        "ausencias", resultado.get("ausencias"),
                        "totalChamadas", resultado.get("totalChamadas"),
                        "totalPresencas", resultado.get("totalPresencas"),
                        "totalAusencias", resultado.get("totalAusencias"),
                        "percentualPresenca", resultado.get("percentualPresenca"),
                        "periodoFiltrado", true,
                        "dataInicio", startDate,
                        "dataFim", endDate));
            } else {
                List<Chamada> chamadas = chamadaService.findByAlunoId(alunoId);
                Long totalPresencas = chamadaService.countPresencas(alunoId);
                return ResponseEntity.ok(Map.of(
                        "chamadas", chamadaMapper.toResponseList(chamadas),
                        "totalPresencas", totalPresencas,
                        "periodoFiltrado", false));
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
        String dataInicio = startDate != null ? startDate : inicio;
        String dataFim = endDate != null ? endDate : fim;
        return findByAluno(alunoId, dataInicio, dataFim);
    }

    @PostMapping("/{id}/presenca/{alunoId}")
    public ResponseEntity<?> marcarPresenca(@PathVariable Long id, @PathVariable Long alunoId) {
        try {
            Chamada chamada = chamadaService.marcarPresenca(id, alunoId);
            return ResponseEntity.ok(chamadaMapper.toResponse(chamada));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/presencas")
    public ResponseEntity<?> marcarPresencas(@PathVariable Long id,
            @Valid @RequestBody MarcarPresencasRequest request) {
        try {
            Chamada chamada = chamadaService.marcarPresencas(id, request.getAlunosIds());
            return ResponseEntity.ok(chamadaMapper.toResponse(chamada));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/presenca/{alunoId}")
    public ResponseEntity<?> removerPresenca(@PathVariable Long id, @PathVariable Long alunoId) {
        try {
            Chamada chamada = chamadaService.removerPresenca(id, alunoId);
            return ResponseEntity.ok(chamadaMapper.toResponse(chamada));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizar(@PathVariable Long id) {
        try {
            Chamada chamada = chamadaService.finalizar(id);
            return ResponseEntity.ok(chamadaMapper.toResponse(chamada));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            chamadaService.delete(id);
            return ResponseEntity.ok("Chamada deletada com sucesso");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

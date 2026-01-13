package bjjapp.controller;

import bjjapp.enums.Faixa;
import bjjapp.service.RequisitosGraduacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/requisitos-graduacao")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000", "https://appbjj.com.br"})
public class RequisitosGraduacaoController {

    private final RequisitosGraduacaoService requisitosGraduacaoService;

    @GetMapping("/faixa/{faixa}")
    public ResponseEntity<List<String>> getRequisitosPorFaixa(@PathVariable String faixa) {
        return ResponseEntity.ok(requisitosGraduacaoService.getRequisitosPorFaixa(faixa));
    }

    @GetMapping("/todas-faixas")
    public ResponseEntity<Map<Faixa, List<String>>> getTodosRequisitos() {
        return ResponseEntity.ok(requisitosGraduacaoService.getTodosRequisitos());
    }
}


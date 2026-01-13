package bjjapp.controller;

import bjjapp.entity.User;
import bjjapp.entity.UserHistorico;
import bjjapp.entity.Turma;
import bjjapp.enums.Faixa;
import bjjapp.enums.Role;
import bjjapp.service.UserService;
import bjjapp.service.RequisitosGraduacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000", "https://appbjj.com.br"})
public class UserController {

    private final UserService userService;
    private final RequisitosGraduacaoService requisitosGraduacaoService;

    @PostMapping("/save")
    public ResponseEntity<UserCreationResponse> save(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.saveWithPlainPassword(user));
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/findByRole")
    public ResponseEntity<List<User>> findByRole(@RequestParam Role role) {
        return ResponseEntity.ok(userService.findByRole(role));
    }

    @GetMapping("/findByUsername")
    public ResponseEntity<List<User>> findByUsername(@RequestParam String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.update(id, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok("Usuário desativado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> getStatus(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getStatus(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/historico/{id}")
    public ResponseEntity<?> getHistorico(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getHistorico(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{faixa}/requisitos/{id}")
    public ResponseEntity<List<String>> getRequisitosPorFaixa(@PathVariable String faixa, @PathVariable Long id) {
        return ResponseEntity.ok(requisitosGraduacaoService.getRequisitosPorFaixa(faixa));
    }

    @GetMapping("/graduacao/{id}")
    public ResponseEntity<?> getCriteriosGraduacao(
            @PathVariable Long id,
            @RequestParam(required = false) String faixa) {
        try {
            User user = userService.findById(id);
            // Usar método que retorna requisitos da PRÓXIMA faixa considerando idade
            List<String> requisitos;
            Faixa faixaRequisitos;
            if (faixa != null) {
                faixaRequisitos = Faixa.valueOf(faixa.toUpperCase());
                requisitos = requisitosGraduacaoService.getRequisitosPorFaixa(faixaRequisitos);
            } else {
                faixaRequisitos = requisitosGraduacaoService.getProximaFaixa(user.getFaixa(), user.getIdade());
                requisitos = requisitosGraduacaoService.getRequisitosParaProximaFaixa(user.getFaixa(), user.getIdade());
            }
            Set<Integer> concluidos = user.getCriteriosConcluidos() != null
                ? user.getCriteriosConcluidos()
                : new java.util.HashSet<>();
            boolean[] criteriosMarcados = new boolean[requisitos.size()];
            int totalConcluidos = 0;
            for (int i = 0; i < requisitos.size(); i++) {
                criteriosMarcados[i] = concluidos.contains(i);
                if (criteriosMarcados[i]) totalConcluidos++;
            }
            boolean prontoParaProximaFaixa = requisitosGraduacaoService.isProntoParaProximaFaixa(
                user.getFaixa(),
                user.getGrau(),
                totalConcluidos,
                requisitos.size(),
                user.getIdade()
            );
            Integer idadeMinimaProximaFaixa = faixaRequisitos != null
                ? requisitosGraduacaoService.getIdadeMinima(faixaRequisitos) : null;
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("requisitos", requisitos);
            response.put("criteriosMarcados", criteriosMarcados);
            response.put("faixaAtual", user.getFaixa().name());
            response.put("faixaRequisitos", faixaRequisitos != null ? faixaRequisitos.name() : null);
            response.put("prontoParaProximaFaixa", prontoParaProximaFaixa);
            response.put("idadeMinimaProximaFaixa", idadeMinimaProximaFaixa);
            response.put("grauAtual", user.getGrau());
            response.put("totalCriterios", requisitos.size());
            response.put("totalConcluidos", totalConcluidos);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/marcar-criterios/{id}")
    public ResponseEntity<?> marcarCriterios(@PathVariable Long id, @RequestBody boolean[] criteriosMarcados) {
        return atualizarChecklistGraduacao(id, criteriosMarcados);
    }

    @PutMapping("/graduacao/{id}")
    public ResponseEntity<?> atualizarChecklistGraduacao(@PathVariable Long id, @RequestBody boolean[] criteriosMarcados) {
        // Converter array de boolean para Set de índices
        Set<Integer> novosIndices = new java.util.HashSet<>();
        for (int i = 0; i < criteriosMarcados.length; i++) {
            if (criteriosMarcados[i]) {
                novosIndices.add(i);
            }
        }
        User user = userService.updateCriterios(id, novosIndices);
        return ResponseEntity.ok(Map.of(
            "mensagem", "Critérios atualizados com sucesso",
            "totalConcluidos", novosIndices.size(),
            "criteriosConcluidos", novosIndices
        ));
    }

    @PostMapping("/conceder-grau/{id}")
    public ResponseEntity<User> concederGrau(@PathVariable Long id) {
        User user = userService.concederGrau(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/credenciais/{id}")
    public ResponseEntity<Map<String, String>> getCredenciais(@PathVariable Long id) {
        User user = userService.findById(id);
        Map<String, String> credenciais = Map.of(
            "username", user.getUsername() != null ? user.getUsername() : "",
            "senha", "(não disponível por segurança)"
        );
        return ResponseEntity.ok(credenciais);
    }

    @PutMapping("/trocar-faixa/{id}")
    public ResponseEntity<User> trocarFaixa(@PathVariable Long id, @RequestBody String novaFaixa) {
        User user = userService.trocarFaixa(id, novaFaixa);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/campos-cadastro")
    public ResponseEntity<?> getCamposCadastro(@RequestParam Integer idade) {
        boolean menorDeIdade = idade != null && idade < 18;
        Map<String, Object> campos = new java.util.HashMap<>();
        campos.put("menorDeIdade", menorDeIdade);
        campos.put("camposObrigatorios", menorDeIdade ?
            Arrays.asList("nome", "dataNascimento", "nomeResponsavel", "whatsappResponsavel", "dataInicioPratica") :
            Arrays.asList("nome", "dataNascimento", "telefoneContato", "dataInicioPratica"));
        campos.put("camposOpcionais", menorDeIdade ?
            Arrays.asList("telefoneContato", "dataUltimaGraduacao") :
            Arrays.asList("nomeResponsavel", "whatsappResponsavel", "dataUltimaGraduacao"));
        return ResponseEntity.ok(campos);
    }
}

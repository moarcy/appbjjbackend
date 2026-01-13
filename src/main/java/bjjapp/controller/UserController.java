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
    public ResponseEntity<User> save(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
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
    public ResponseEntity<List<String>> getChecklistGraduacao(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(requisitosGraduacaoService.getRequisitosPorFaixa(user.getFaixa()));
    }

    @PutMapping("/marcar-criterios/{id}")
    public ResponseEntity<User> marcarCriterios(@PathVariable Long id, @RequestBody List<Integer> criterios) {
        User user = userService.updateCriterios(id, Set.copyOf(criterios));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/graduacao/{id}")
    public ResponseEntity<User> atualizarChecklistGraduacao(@PathVariable Long id, @RequestBody List<Integer> criterios) {
        User user = userService.updateCriterios(id, Set.copyOf(criterios));
        return ResponseEntity.ok(user);
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

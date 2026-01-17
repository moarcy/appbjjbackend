package bjjapp.controller;

import bjjapp.config.JwtUtil;
import bjjapp.dto.request.LoginRequest;
import bjjapp.entity.User;
import bjjapp.enums.Role;
import bjjapp.service.UserAuthService;
import bjjapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:5173", "http://localhost:3000",
        "https://appbjj.com.br", "https://appbjjfront-hvhk.vercel.app/" })
public class AuthController {

    private final UserService userService;
    private final UserAuthService userAuthService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        System.out
                .println("[DEBUG] AuthController.login: Iniciando login para username: " + loginRequest.getUsername());

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        System.out.println("[DEBUG] AuthController.login: Buscando usuário: " + username);
        Optional<User> userOpt = userAuthService.findByUsername(username);

        if (userOpt.isEmpty()) {
            System.out.println("[DEBUG] AuthController.login: Usuário não encontrado: " + username);
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        User user = userOpt.get();
        System.out.println("[DEBUG] AuthController.login: Usuário encontrado: " + user.getUsername());

        System.out.println("[DEBUG] AuthController.login: Verificando senha");
        if (!userAuthService.checkPassword(password, user.getPassword())) {
            System.out.println("[DEBUG] AuthController.login: Senha incorreta para: " + username);
            return ResponseEntity.badRequest().body("Senha incorreta");
        }

        System.out.println("[DEBUG] AuthController.login: Gerando token");
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        System.out.println("[DEBUG] AuthController.login: Token gerado, login bem-sucedido para: " + username);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "nome", user.getNome(),
                        "username", user.getUsername(),
                        "role", user.getRole().name()),
                "message", "Login realizado com sucesso"));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> register(@RequestParam String nome, @RequestParam Role role) {
        User user = userService.createProfessor(nome);
        user.setRole(role);
        return ResponseEntity.ok(user);
    }
}

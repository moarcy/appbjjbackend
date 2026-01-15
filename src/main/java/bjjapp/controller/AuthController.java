package bjjapp.controller;

import bjjapp.config.JwtUtil;
import bjjapp.entity.User;
import bjjapp.enums.Role;
import bjjapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000", "https://appbjj.com.br", "https://appbjjfront-hvhk.vercel.app/"})
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        System.out.println("[DEBUG] AuthController.login: Iniciando login para username: " + credentials.get("username"));

        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            System.out.println("[DEBUG] AuthController.login: Username ou password nulos");
            return ResponseEntity.badRequest().body("Username e password são obrigatórios");
        }

        Optional<User> userOpt = userService.findByUsernameAuth(username);
        if (userOpt.isEmpty()) {
            System.out.println("[DEBUG] AuthController.login: Usuário não encontrado: " + username);
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        User user = userOpt.get();
        if (!userService.checkPassword(password, user.getPassword())) {
            System.out.println("[DEBUG] AuthController.login: Senha incorreta para: " + username);
            return ResponseEntity.badRequest().body("Senha incorreta");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        System.out.println("[DEBUG] AuthController.login: Login bem-sucedido para: " + username);

        return ResponseEntity.ok(Map.of(
            "token", token,
            "user", Map.of(
                "id", user.getId(),
                "nome", user.getNome(),
                "username", user.getUsername(),
                "role", user.getRole().name()
            ),
            "message", "Login realizado com sucesso"
        ));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> register(@RequestParam String nome, @RequestParam Role role) {
        User user = userService.createProfessor(nome);
        user.setRole(role);
        return ResponseEntity.ok(user);
    }
}

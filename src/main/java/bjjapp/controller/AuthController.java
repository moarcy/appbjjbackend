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
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username e password são obrigatórios");
        }

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        User user = userOpt.get();
        if (!userService.checkPassword(password, user.getPassword())) {
            return ResponseEntity.badRequest().body("Senha incorreta");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

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

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
        try {
            System.out.println("[DEBUG] AuthController.login: Iniciando login para username: " + credentials.get("username"));

            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                System.out.println("[DEBUG] AuthController.login: Username ou password nulos");
                return ResponseEntity.badRequest().body("Username e password são obrigatórios");
            }

            System.out.println("[DEBUG] AuthController.login: Buscando usuário: " + username);
            Optional<User> userOpt = userService.findByUsernameAuth(username);
            if (userOpt.isEmpty()) {
                System.out.println("[DEBUG] AuthController.login: Usuário não encontrado: " + username);
                return ResponseEntity.badRequest().body("Usuário não encontrado");
            }

            User user = userOpt.get();
            System.out.println("[DEBUG] AuthController.login: Usuário encontrado: " + user.getUsername() + ", role: " + user.getRole() + ", school: " + (user.getSchool() != null ? user.getSchool().getName() : "null"));

            System.out.println("[DEBUG] AuthController.login: Verificando senha");
            if (!userService.checkPassword(password, user.getPassword())) {
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
                    "role", user.getRole().name()
                ),
                "message", "Login realizado com sucesso"
            ));
        } catch (Exception e) {
            System.err.println("[ERROR] AuthController.login: Erro durante login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno do servidor: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> register(@RequestParam String nome, @RequestParam Role role) {
        User user = userService.createProfessor(nome);
        user.setRole(role);
        return ResponseEntity.ok(user);
    }
}

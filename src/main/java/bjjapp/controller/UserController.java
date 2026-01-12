package bjjapp.controller;

import bjjapp.entity.User;
import bjjapp.enums.Role;
import bjjapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000", "https://appbjj.com.br"})
public class UserController {

    private final UserService userService;

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
        log.info("Desativando usuário ID: {}", id);
        try {
            userService.delete(id);
            return ResponseEntity.ok("Usuário desativado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

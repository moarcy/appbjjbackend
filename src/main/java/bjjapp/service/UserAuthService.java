package bjjapp.service;

import bjjapp.entity.User;
import bjjapp.entity.UserPlainPassword;
import bjjapp.enums.Role;
import bjjapp.repository.UserPlainPasswordRepository;
import bjjapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final UserPlainPasswordRepository userPlainPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Gera um username único baseado no nome
     */
    public String generateUsername(String nome) {
        String baseUsername = nome.toLowerCase().replace(" ", ".");
        String username = baseUsername;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }
        return username;
    }

    /**
     * Gera uma senha aleatória de 8 caracteres
     */
    public String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Transactional
    public void generateCredentialsForNewUser(User user) {
        if (user.getId() == null && user.getUsername() == null) {
            String username = generateUsername(user.getNome());
            String rawPassword = generatePassword();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setPlainPassword(rawPassword); // Transient field

            // Persist plain password immediately if user is already saved?
            // Usually this is called BEFORE save.
        }
    }

    @Transactional
    public void savePlainPassword(User user, String rawPassword) {
        if (user.getId() == null)
            return; // Must satisfy FK

        UserPlainPassword credenciais = userPlainPasswordRepository.findByUserId(user.getId());
        if (credenciais == null && rawPassword != null) {
            credenciais = new UserPlainPassword();
            credenciais.setUserId(user.getId());
            credenciais.setUsername(user.getUsername());
            credenciais.setPlainPassword(rawPassword);
            userPlainPasswordRepository.save(credenciais);
        }
    }

    public UserPlainPassword getCredenciais(Long userId) {
        UserPlainPassword credenciais = userPlainPasswordRepository.findByUserId(userId);
        if (credenciais == null) {
            // Logic to regenerate if missing (legacy support)
            // But this requires circular dependency to UserService.findById needed?
            // Auth service should ideally just deal with Auth entities.
            // Let's keep regeneration logic in specific flow or move findById logic here?
            // Simply return null or throw exception, let controller or upper layer handle
            // regeneration if needed.
            // Or use UserRepository directly.
            // Implementation copied from UserService with improvement:
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.getUsername() != null) {
                    // Regenerate
                    return regenerateCredentials(user);
                }
            }
        }
        return credenciais;
    }

    @Transactional
    public UserPlainPassword regenerateCredentials(User user) {
        UserPlainPassword credenciais = new UserPlainPassword();
        credenciais.setUserId(user.getId());
        credenciais.setUsername(user.getUsername());

        String newPassword = generatePassword();
        credenciais.setPlainPassword(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return userPlainPasswordRepository.save(credenciais);
    }
}

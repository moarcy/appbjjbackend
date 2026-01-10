package bjjapp.repository;

import bjjapp.entity.UserPlainPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPlainPasswordRepository extends JpaRepository<UserPlainPassword, Long> {
    UserPlainPassword findByUserId(Long userId);
}


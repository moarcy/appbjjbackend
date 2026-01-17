package bjjapp.dto.request;

import bjjapp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Request DTO for creating or updating a user.
 * Currently wraps User entity for compatibility, pending full Mapper
 * implementation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private User user;

    private Set<Long> turmasIds;

    // Helper accessors to match Record style for easier refactoring
    public User user() {
        return user;
    }

    public Set<Long> turmasIds() {
        return turmasIds;
    }
}

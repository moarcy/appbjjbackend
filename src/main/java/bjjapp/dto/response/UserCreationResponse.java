package bjjapp.dto.response;

import bjjapp.entity.User;

public record UserCreationResponse(
        User user,
        String username,
        String password) {
}

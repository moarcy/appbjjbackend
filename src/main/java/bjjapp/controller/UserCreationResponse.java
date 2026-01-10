package bjjapp.controller;

import bjjapp.entity.User;

public record UserCreationResponse(
    User user,
    String username,
    String password
) {}

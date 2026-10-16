package auth_service.auth.dto;

public record LoginRequest (
    String username,
    String password
    )
{}

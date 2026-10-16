package auth_service.auth.dto;

public record TokenResp(

    String accessToken,
    String tokenType,
    long expiresIn
)
{}

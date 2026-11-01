package auth_service.auth.controller;

import auth_service.auth.dto.LoginRequest;
import auth_service.auth.dto.TokenResp;
import auth_service.auth.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expose l'endpoint d'authentification de l'organisateur.
 *
 * <p>Après vérification des identifiants, le contrôleur retourne un jeton JWT
 * permettant d'accéder aux microservices protégés.</p>
 */
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Authentifie l'organisateur et retourne un jeton d'accès JWT.
     *
     * @param request identifiants de connexion fournis par le client
     * @return HTTP 200 avec le jeton JWT, HTTP 400 si la requête est invalide,
     *         ou HTTP 401 si les identifiants sont incorrects
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request == null
                || request.username() == null
                || request.username().isBlank()
                || request.password() == null
                || request.password().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.username(),
                            request.password()
                    )
            );

            TokenResp token = jwtService.createToken(authentication);

            return ResponseEntity.ok(token);
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

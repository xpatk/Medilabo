package auth_service.auth.service;

import auth_service.auth.dto.TokenResp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Crée les jetons JWT utilisés pour sécuriser les échanges entre le frontend
 * et les microservices Medilabo.
 *
 * <p>Les jetons sont signés avec l'algorithme RSA RS256 et contiennent
 * l'identifiant de l'utilisateur, son rôle ainsi que leur date d'expiration.</p>
 */
@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Duration accessTokenTtl;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.access-token-ttl}") Duration accessTokenTtl
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.accessTokenTtl = accessTokenTtl;
    }

    /**
     * Génère un JWT signé pour un utilisateur authentifié.
     *
     * @param authentication résultat de l'authentification Spring Security
     * @return réponse contenant le jeton, son type et sa durée de validité
     */
    public TokenResp createToken(Authentication authentication) {

        Instant now = Instant.now();

        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenTtl))
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId("medilabo-auth-key-1")
                .build();

        String accessToken = jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();

        return new TokenResp(
                accessToken,
                "Bearer",
                accessTokenTtl.toSeconds()
        );
    }
}
package auth_service.auth.service;

import auth_service.auth.dto.TokenResp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Test
    void createsBearerTokenWithOrganizerRole() {
        JwtService jwtService = new JwtService(
                jwtEncoder,
                "medilabo-auth-test",
                Duration.ofMinutes(15)
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "organizer",
                        "ignored-password",
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ORGANIZER"),
                                new SimpleGrantedAuthority("SCOPE_read")
                        )
                );

        Instant now = Instant.now();

        Jwt encodedJwt = new Jwt(
                "encoded-jwt-value",
                now,
                now.plus(Duration.ofMinutes(15)),
                Map.of("alg", "RS256"),
                Map.of("sub", "organizer")
        );

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(encodedJwt);

        TokenResp result = jwtService.createToken(authentication);

        assertThat(result.accessToken()).isEqualTo("encoded-jwt-value");
        assertThat(result.tokenType()).isEqualTo("Bearer");
        assertThat(result.expiresIn()).isEqualTo(900);

        ArgumentCaptor<JwtEncoderParameters> parametersCaptor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(parametersCaptor.capture());

        JwtClaimsSet claims = parametersCaptor.getValue().getClaims();
        JwsHeader header = parametersCaptor.getValue().getJwsHeader();

        assertThat(claims.getClaims().get("iss"))
                .isEqualTo("medilabo-auth-test");
        assertThat(claims.getClaims().get("sub"))
                .isEqualTo("organizer");
        assertThat(claims.getClaims().get("roles"))
                .isEqualTo(List.of("ORGANIZER"));

        assertThat(header.getAlgorithm())
                .isEqualTo(SignatureAlgorithm.RS256);
        assertThat(header.getKeyId())
                .isEqualTo("medilabo-auth-key-1");
    }
}
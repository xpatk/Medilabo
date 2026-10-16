package auth_service.auth.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtConfig {

    @Bean
    public RSAPublicKey rsaPublicKey() throws IOException {
        return RsaKeyConverters.x509().convert(
                new ClassPathResource(
                        "keys/public.pem"
                ).getInputStream()
        );
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey() throws IOException {
        return RsaKeyConverters.pkcs8().convert(
                new ClassPathResource(
                        "keys/private.pem"
                ).getInputStream()
        );
    }

    @Bean
    public JwtEncoder jwtEncoder(
            RSAPublicKey publicKey,
            RSAPrivateKey privateKey
    ) {
        JWK jwk = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("medilabo-auth-key-1")
                .build();

        ImmutableJWKSet<SecurityContext> jwkSource =
                new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwkSource);
    }
}

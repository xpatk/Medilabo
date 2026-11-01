package auth_service.auth.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configure l'authentification de l'organisateur dans l'Auth Service.
 *
 * <p>L'utilisateur est conservé en mémoire et son mot de passe est chiffré
 * avec BCrypt. Seul l'endpoint de connexion est accessible sans JWT.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Crée l'utilisateur organisateur conservé en mémoire.
     *
     * @param passwordEncoder encodeur BCrypt utilisé pour chiffrer le mot de passe
     * @param organizerPassword mot de passe fourni par la configuration sécurisée
     * @return service contenant l'utilisateur organisateur
     */
    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${app.security.organizer-password}") String organizerPassword) {
        UserDetails organizer = User.builder()
                .username("organizer")
                .password(passwordEncoder.encode(organizerPassword))
                .roles("ORGANIZER")
                .build();

        return new InMemoryUserDetailsManager(organizer);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Définit les règles de sécurité de l'Auth Service.
     *
     * <p>Seule la requête POST vers {@code /api/auth/login} est publique.
     * Les autres requêtes sont refusées.</p>
     *
     * @param http configuration de sécurité Spring
     * @return chaîne de filtres de sécurité
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .anyRequest().denyAll()
                )
                        .build();
    }
}
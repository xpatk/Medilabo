package auth_service.auth.controller;

import auth_service.auth.dto.LoginRequest;
import auth_service.auth.dto.TokenResp;
import auth_service.auth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    void returnsOkWithTokenForValidCredentials() {
        Authentication authentication = mock(Authentication.class);
        TokenResp expectedToken = new TokenResp(
                "test-access-token",
                "Bearer",
                900
        );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(jwtService.createToken(authentication))
                .thenReturn(expectedToken);

        ResponseEntity<?> response = authController.login(
                new LoginRequest("organizer", "correct-password")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expectedToken);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(
                        UsernamePasswordAuthenticationToken.class
                );

        verify(authenticationManager).authenticate(tokenCaptor.capture());

        assertThat(tokenCaptor.getValue().getName())
                .isEqualTo("organizer");
        assertThat(tokenCaptor.getValue().getCredentials())
                .isEqualTo("correct-password");

        verify(jwtService).createToken(authentication);
    }

    @Test
    void returnsUnauthorizedForInvalidCredentials() {
        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<?> response = authController.login(
                new LoginRequest("organizer", "wrong-password")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(jwtService);
    }

    @Test
    void returnsBadRequestWhenCredentialsAreMissing() {
        ResponseEntity<?> response = authController.login(
                new LoginRequest("", "")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(authenticationManager, jwtService);
    }
}
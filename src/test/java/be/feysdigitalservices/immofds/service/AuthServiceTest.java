package be.feysdigitalservices.immofds.service;

import be.feysdigitalservices.immofds.TestDataFactory;
import be.feysdigitalservices.immofds.config.JwtConfig;
import be.feysdigitalservices.immofds.domain.entity.RefreshToken;
import be.feysdigitalservices.immofds.domain.entity.User;
import be.feysdigitalservices.immofds.dto.request.LoginRequest;
import be.feysdigitalservices.immofds.dto.request.RefreshTokenRequest;
import be.feysdigitalservices.immofds.dto.response.AuthResponse;
import be.feysdigitalservices.immofds.dto.response.UserResponse;
import be.feysdigitalservices.immofds.exception.InvalidTokenException;
import be.feysdigitalservices.immofds.mapper.UserMapper;
import be.feysdigitalservices.immofds.repository.RefreshTokenRepository;
import be.feysdigitalservices.immofds.repository.UserRepository;
import be.feysdigitalservices.immofds.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_shouldReturnAuthResponse() {
        LoginRequest request = TestDataFactory.createLoginRequest();
        User user = TestDataFactory.createUser();
        Authentication authentication = mock(Authentication.class);
        UserResponse userResponse = mock(UserResponse.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(tokenProvider.getExpirationMs()).thenReturn(900000L);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtConfig.refreshExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> {
            RefreshToken rt = inv.getArgument(0);
            rt.setId(1L);
            return rt;
        });
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        AuthResponse result = authService.login(request);

        assertThat(result.accessToken()).isEqualTo("jwt-token");
        assertThat(result.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void refresh_expiredToken_shouldThrow() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("expired-token");
        refreshToken.setExpiryDate(Instant.now().minusSeconds(3600));

        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> authService.refresh(new RefreshTokenRequest("expired-token")))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("expiré");
    }

    @Test
    void refresh_invalidToken_shouldThrow() {
        when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(new RefreshTokenRequest("invalid")))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("invalide");
    }
}

package be.feysdigitalservices.immofds.service;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
                       RefreshTokenRepository refreshTokenRepository, UserRepository userRepository,
                       UserMapper userMapper, JwtConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtConfig = jwtConfig;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        String accessToken = tokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidTokenException("Utilisateur non trouvé"));

        RefreshToken refreshToken = createRefreshToken(user);
        UserResponse userResponse = userMapper.toResponse(user);

        return new AuthResponse(accessToken, refreshToken.getToken(), tokenProvider.getExpirationMs(), userResponse);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidTokenException("Refresh token invalide"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token expiré");
        }

        User user = refreshToken.getUser();
        String accessToken = tokenProvider.generateToken(user.getEmail());
        UserResponse userResponse = userMapper.toResponse(user);

        return new AuthResponse(accessToken, refreshToken.getToken(), tokenProvider.getExpirationMs(), userResponse);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    private RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtConfig.refreshExpirationMs()));

        return refreshTokenRepository.save(refreshToken);
    }
}

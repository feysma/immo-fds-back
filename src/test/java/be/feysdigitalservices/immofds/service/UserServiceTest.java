package be.feysdigitalservices.immofds.service;

import be.feysdigitalservices.immofds.TestDataFactory;
import be.feysdigitalservices.immofds.domain.entity.User;
import be.feysdigitalservices.immofds.dto.request.UserCreateRequest;
import be.feysdigitalservices.immofds.dto.response.UserResponse;
import be.feysdigitalservices.immofds.exception.DuplicateResourceException;
import be.feysdigitalservices.immofds.exception.ResourceNotFoundException;
import be.feysdigitalservices.immofds.mapper.UserMapper;
import be.feysdigitalservices.immofds.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldEncodePasswordAndSave() {
        UserCreateRequest request = TestDataFactory.createUserRequest();
        User user = TestDataFactory.createUser();
        UserResponse response = mock(UserResponse.class);

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.createUser(request);

        assertThat(result).isNotNull();
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_duplicateEmail_shouldThrow() {
        UserCreateRequest request = TestDataFactory.createUserRequest();

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email existe déjà");
    }

    @Test
    void getUserById_notFound_shouldThrow() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = TestDataFactory.createUser();
        UserResponse response = mock(UserResponse.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getUserById(1L);

        assertThat(result).isNotNull();
    }
}

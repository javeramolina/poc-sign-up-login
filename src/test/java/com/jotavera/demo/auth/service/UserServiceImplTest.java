package com.jotavera.demo.auth.service;

import com.jotavera.demo.auth.dto.request.PhoneRequest;
import com.jotavera.demo.auth.dto.request.SignUpRequest;
import com.jotavera.demo.auth.dto.response.SignUpResponse;
import com.jotavera.demo.auth.dto.response.UserResponse;
import com.jotavera.demo.auth.exception.UserAlreadyExistsException;
import com.jotavera.demo.auth.model.Phone;
import com.jotavera.demo.auth.model.User;
import com.jotavera.demo.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testSignUp_NewUser_Success() {
        SignUpRequest request = signUpRequestObject();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        SignUpResponse response = userService.signUp(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    private SignUpRequest signUpRequestObject() {
        PhoneRequest phone = new PhoneRequest();
        phone.setNumber(123456789);
        phone.setCitycode(1);
        phone.setContrycode("+56");

        SignUpRequest request = new SignUpRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("abcdeF12");
        request.setPhones(List.of(phone));

        return request;
    }

    @Test
    void testSignUp_ExistingUser_ThrowsException() {
        SignUpRequest request = new SignUpRequest();

        request.setEmail("existing@example.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.signUp(request));
    }

    @Test
    void login_ShouldReturnUserResponse_WhenTokenIsValid() {

        String token = "Bearer dummy.token.here";
        String email = "john.doe@example.com";
        String newToken = "new.jwt.token";

        User user = User.builder()
                .idUser(UUID.randomUUID())
                .name("John Doe")
                .email(email)
                .password(new BCryptPasswordEncoder().encode("Abcde123"))
                .token(token)
                .isActive(true)
                .created(LocalDateTime.now().minusDays(1))
                .phones(List.of(
                        Phone.builder().number(123456789).citycode(2).contrycode("+56").build()
                ))
                .build();

        when(jwtService.extractSubject("dummy.token.here")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(email)).thenReturn(newToken);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.login(token);

        assertNotNull(response);
        assertEquals(email, response.getEmail());
        assertEquals(newToken, response.getToken());
        assertEquals(1, response.getPhones().size());
        verify(userRepository).save(any(User.class));
    }
}

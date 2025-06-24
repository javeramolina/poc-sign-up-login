package com.jotavera.demo.auth.repository;

import com.jotavera.demo.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {

    @SuppressWarnings("FieldMayBeFinal")
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {

        User user = User.builder()
                .idUser(UUID.randomUUID())
                .name("Jesus Vera")
                .email("jesus.vera@example.com")
                .password("Abcdef12")
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .build();

        userRepository.save(user);
        Optional<User> result = userRepository.findByEmail("jesus.vera@example.com");

        assertTrue(result.isPresent());
        assertEquals("Jesus Vera", result.get().getName());
    }

    @Test
    void shouldReturnEmptyIfUserNotFoundByEmail() {

        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }
}

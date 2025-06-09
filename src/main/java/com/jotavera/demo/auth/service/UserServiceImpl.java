package com.jotavera.demo.auth.service;

import com.jotavera.demo.auth.dto.request.SignUpRequest;
import com.jotavera.demo.auth.dto.response.PhoneDTO;
import com.jotavera.demo.auth.dto.response.SignUpResponse;
import com.jotavera.demo.auth.dto.response.UserResponse;
import com.jotavera.demo.auth.exception.UserAlreadyExistsException;
import com.jotavera.demo.auth.model.Phone;
import com.jotavera.demo.auth.model.User;
import com.jotavera.demo.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        Optional <User> user = userRepository.findByEmail(signUpRequest.getEmail());
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("The user already exists");
        }
        User userCreateResponse = createUser(signUpRequest);
        SignUpResponse signUpResponse = generateResponse(userCreateResponse);

        return signUpResponse;
    }

    private SignUpResponse generateResponse(User userCreateResponse) {
        return SignUpResponse.builder()
                .id(userCreateResponse.getIdUser())
                .created(userCreateResponse.getCreated())
                .lastLogin(userCreateResponse.getLastLogin())
                .token(userCreateResponse.getToken())
                .isActive(userCreateResponse.isActive())
                .build();

    }

    private User createUser(SignUpRequest signUpRequest) {

        User user = User.builder()
                .idUser(UUID.randomUUID())
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .token(jwtService.generateToken(signUpRequest.getEmail()))
                .isActive(true)
                .build();

        List<Phone> phones = signUpRequest.getPhones().stream()
                .map(p -> Phone.builder()
                        .number(p.getNumber())
                        .citycode(p.getCitycode())
                        .contrycode(p.getContrycode())
                        .user(user)
                        .build())
                .collect(Collectors.toList());

        user.setPhones(phones);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }
    @Transactional
    public UserResponse login(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractSubject(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newToken = jwtService.generateToken(email);
        user.setToken(newToken);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        List<PhoneDTO> phoneDTOs = user.getPhones().stream()
                .map(p -> PhoneDTO.builder()
                        .number(p.getNumber())
                        .citycode(p.getCitycode())
                        .contrycode(p.getContrycode())
                        .build())
                .collect(Collectors.toList());


        UserResponse response = UserResponse.builder()
                .id(user.getIdUser())
                .created(user.getCreated())
                .lastLogin(user.getLastLogin())
                .token(user.getToken())
                .isActive(user.isActive())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phones(phoneDTOs)
                .build();
    return response;
    }
}

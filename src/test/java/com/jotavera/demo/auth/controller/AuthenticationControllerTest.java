package com.jotavera.demo.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jotavera.demo.auth.dto.request.SignUpRequest;
import com.jotavera.demo.auth.dto.response.SignUpResponse;
import com.jotavera.demo.auth.dto.response.UserResponse;
import com.jotavera.demo.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }


    @Test
    void signUp_shouldReturnBadRequestDueToInvalidPassword() throws Exception {

        SignUpRequest request = new SignUpRequest();
        request.setName("Jesus Vera");
        request.setEmail("jesus.vera@example.com");
        request.setPassword("abcdef12");

        String jsonRequest = objectMapper.writeValueAsString(request);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnSignUpResponseWhenSignUpIsSuccessful() throws Exception {

        SignUpRequest request = new SignUpRequest();
        request.setName("Jesus Vera");
        request.setEmail("jesus.vera@example.com");
        request.setPassword("Abcdef12");

        SignUpResponse response = SignUpResponse.builder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("sample.jwt.token")
                .isActive(true)
                .build();

        Mockito.when(userService.signUp(any(SignUpRequest.class))).thenReturn(response);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("sample.jwt.token"))
                .andExpect(jsonPath("$.isActive").value(true));
    }



    @Test
    void shouldReturnUserResponseWhenLoginIsSuccessful() throws Exception {

        String token = "Bearer sample.jwt.token";

        UserResponse userResponse = UserResponse.builder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("new.jwt.token")
                .isActive(true)
                .name("Jesus Vera")
                .email("jesus.vera@example.com")
                .build();

        Mockito.when(userService.login(token)).thenReturn(userResponse);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/login")
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new.jwt.token"))
                .andExpect(jsonPath("$.email").value("jesus.vera@example.com"));
    }


}
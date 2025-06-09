package com.jotavera.demo.auth.service;

import com.jotavera.demo.auth.dto.request.SignUpRequest;
import com.jotavera.demo.auth.dto.response.SignUpResponse;
import com.jotavera.demo.auth.dto.response.UserResponse;

public interface UserService {
    UserResponse login(String authHeader);
    SignUpResponse signUp(SignUpRequest request);
}

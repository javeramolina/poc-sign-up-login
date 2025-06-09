package com.jotavera.demo.auth.dto.request;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class SignUpRequest {

    private String name;

    @NotBlank(message = "Email must not be blank")
    @Pattern(
            regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
            message = "Email must be a valid format (example@domain.com)"
    )
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Pattern(
            regexp = "^(?=(?:[^A-Z]*[A-Z][^A-Z]*)$)(?=(?:\\D*\\d\\D*\\d\\D*)$)[a-zA-Z\\d]{8,12}$",
            message = "Password must contain exactly one uppercase letter, exactly two digits, only lowercase letters otherwise, and be 8–12 characters long."
    )
    private String password;

    private List<PhoneRequest> phones;

}

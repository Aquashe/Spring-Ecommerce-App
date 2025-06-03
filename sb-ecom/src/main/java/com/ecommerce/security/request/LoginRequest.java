package com.ecommerce.security.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    @Size(min = 3, message = "Username should contain atleast 3 characters")
    private String username;
    @NotBlank
    @Size(min = 3, message = "Password should contain atleast 3 characters")
    private String password;
}

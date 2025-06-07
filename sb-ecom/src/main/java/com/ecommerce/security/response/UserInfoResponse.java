package com.ecommerce.security.response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private  String jwtToken;
    private String username;
    private List<String> roles;

    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.userId = id;
        this.username = username;
        this.roles = roles;
    }
}

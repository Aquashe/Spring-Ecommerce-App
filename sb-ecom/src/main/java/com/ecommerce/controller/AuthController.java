package com.ecommerce.controller;

import com.ecommerce.model.AppRole;
import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.jwt.JwtUtils;
import com.ecommerce.security.request.LoginRequest;
import com.ecommerce.security.request.SignUpRequest;
import com.ecommerce.security.response.MessageResponse;
import com.ecommerce.security.response.UserInfoResponse;
import com.ecommerce.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication ;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            Map<String,Object> map = new HashMap<>();
            map.put("Message ","Bad Credentials");
            map.put("Status",false);

            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(item -> item.getAuthority())
                .toList();
        UserInfoResponse loginResponse = new UserInfoResponse(userDetails.getId(), jwtCookie.toString(),userDetails.getUsername(),roles);
        return  ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername()))
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error : Username is already taken"));

        if (userRepository.existsByEmail(signUpRequest.getEmail()))
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error : Email is already taken"));

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()));

        Set<String> signUpRequestRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if(signUpRequestRoles == null){
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(()-> new RuntimeException("Error: Role not found"));
            roles.add(userRole);
        }else{
            signUpRequestRoles.forEach(role -> {
                switch (role) {
                    case "admin":   Role adminRole = roleRepository
                                        .findByRoleName(AppRole.ROLE_ADMIN)
                                                .orElseThrow(()-> new RuntimeException("Error: Role not found"));
                                    roles.add(adminRole);
                                    break;
                    case "seller":  Role sellerRole = roleRepository
                                        .findByRoleName(AppRole.ROLE_SELLER)
                                        .orElseThrow(()-> new RuntimeException("Error: Role not found"));
                                    roles.add(sellerRole);
                                    break;
                    default:        Role userRole = roleRepository
                                        .findByRoleName(AppRole.ROLE_USER)
                                        .orElseThrow(()-> new RuntimeException("Error: Role not found"));
                                    roles.add(userRole);
                                    break;
                }
            });
        }
        user.setRoleSet(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @GetMapping("/username")
    public String currrentUserName(Authentication authentication) {
        if(authentication != null)
            return authentication.getName();
        return null;
    }

    @GetMapping("/userdetails")
    public ResponseEntity<UserInfoResponse> currrentUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(item -> item.getAuthority())
                .toList();
        UserInfoResponse loginResponse = new UserInfoResponse(userDetails.getId(),userDetails.getUsername(),roles);
        return  ResponseEntity
                .ok()
                .body(loginResponse);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie responseCookie = jwtUtils.generateCleanCookie();
        return  ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new MessageResponse("Successfully logged out"));
    }

}




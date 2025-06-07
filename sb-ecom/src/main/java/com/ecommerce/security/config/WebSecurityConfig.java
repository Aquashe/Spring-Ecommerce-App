package com.ecommerce.security.config;

import com.ecommerce.model.AppRole;
import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.jwt.AuthEntryPointJwt;
import com.ecommerce.security.jwt.AuthTokenFilter;
import com.ecommerce.security.services.UserDetailsServicesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServicesImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) ->
                requests.requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        //.requestMatchers("/api/public/**").permitAll()
                        //.requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()));
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security/**",
                "/swagger-ui.html/**",
                "/webjars/**"
        ));
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            //Retrieve or create Role
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(()->{
                        Role user = new Role(AppRole.ROLE_USER);
                        return  roleRepository.save(user);
                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(()->{
                        Role user = new Role(AppRole.ROLE_ADMIN);
                        return  roleRepository.save(user);
                    });

            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(()->{
                        Role user = new Role(AppRole.ROLE_SELLER);
                        return  roleRepository.save(user);
                    });



            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, adminRole, sellerRole);




            //Crate users if not already present
            if(!userRepository.existsByUsername("user1")){
                User user1 = new User("user1","user1@example.com",passwordEncoder.encode("password1"));
                userRepository.save(user1);
            }
            if(!userRepository.existsByUsername("seller1")){
                User seller1 = new User("seller1","seller1@example.com",passwordEncoder.encode("password2"));
                userRepository.save(seller1);
            }
            if(!userRepository.existsByUsername("admin1")){
                User admin1 = new User("admin1","admin1@example.com",passwordEncoder.encode("password3"));
                userRepository.save(admin1);
            }




            //Update roles for existing users
            userRepository.findByUsername("user1").ifPresent(user -> {
                user.setRoleSet(userRoles);
                userRepository.save(user);
            });
            userRepository.findByUsername("seller1").ifPresent(user -> {
                user.setRoleSet(sellerRoles);
                userRepository.save(user);
            });
            userRepository.findByUsername("admin1").ifPresent(user -> {
                user.setRoleSet(adminRoles);
                userRepository.save(user);
            });




        };
    }

}

package com.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "username")
    private String username;

    @NotBlank
    @Size(min = 3, max = 50)
    @Email
    @Column(name = "email")
    private String email;

    @NotBlank
    @Column(name = "password")
    private String password;

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_Id")
            ,inverseJoinColumns = @JoinColumn(name = "role_Id"))
    private Set<Role> roleSet = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "user" , cascade = {CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval = true)
    private Set<Product> productSet = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name = "user_address",joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "address_id"))
    private List<Address> addressList = new ArrayList<>();

    public User(@NotBlank @Size(min = 3, max = 50) String username, @NotBlank @Size(min = 3, max = 20) @Email String email, String encode) {
        this.username = username;
        this.email = email;
        this.password = encode;
    }
}

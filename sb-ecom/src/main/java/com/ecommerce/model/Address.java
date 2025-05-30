package com.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int addressId;

    @NotBlank
    @Size(min = 4, max = 50, message = "Street name must be atleast 4 characters")
    private String street;

    @NotBlank
    @Size(min = 4, max = 50, message = "Building name must be atleast 4 characters")
    private String building;

    @NotBlank
    @Size(min = 4, max = 50, message = "City name must be atleast 4 characters")
    private String city;

    @NotBlank
    @Size(min = 4, max = 50, message = "State name must be atleast 4 characters")
    private String state;

    @NotBlank
    @Size(min = 2, max = 50, message = "Country name must be atleast 2 characters")
    private String country;

    @NotBlank
    @Size(min = 6, max = 50, message = "Pincode  must be atleast 5 characters")
    private String pincode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addressList")
    private List<User> users = new ArrayList<>();
}

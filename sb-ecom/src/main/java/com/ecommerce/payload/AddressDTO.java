package com.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private int addressId;
    private String street;
    private String building;
    private String city;
    private String state;
    private String country;
    private String pincode;
}

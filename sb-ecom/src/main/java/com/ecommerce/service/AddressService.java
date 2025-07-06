package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.payload.AddressDTO;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);
}

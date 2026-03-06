package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.AddressResponseDTO;
import com.Kalakriti.Kalakriti.entity.Address;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.service.AddressService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public AddressResponseDTO addAddress(@AuthenticationPrincipal User user,
                                         @RequestBody Address address) {
        return addressService.addAddress(user, address);
    }

    @GetMapping
    public List<Address> getAddresses(@AuthenticationPrincipal User user) {
        return addressService.getUserAddresses(user);
    }

    @DeleteMapping("/{id}")
    public String deleteAddress(@AuthenticationPrincipal User user,
                                @PathVariable Long id) {
        addressService.deleteAddress(user, id);
        return "Address deleted successfully";
    }
}
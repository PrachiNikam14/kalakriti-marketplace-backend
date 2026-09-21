package com.Kalakriti.Kalakriti.controller;

import com.Kalakriti.Kalakriti.dto.AddressResponseDTO;
import com.Kalakriti.Kalakriti.entity.Address;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.service.AddressService;
import org.springframework.http.ResponseEntity;
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
    public AddressResponseDTO addAddress(
            @AuthenticationPrincipal User user,
            @RequestBody Address address
    ) {
        return addressService.addAddress(user, address);
    }

    @GetMapping
    public List<AddressResponseDTO> getAddresses(
            @AuthenticationPrincipal User user
    ) {
        return addressService.getUserAddressDTOs(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        addressService.deleteAddress(user, id);

        return ResponseEntity.ok(
                "Address deleted successfully"
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(
            @PathVariable Long id,
            @RequestBody Address updatedAddress,
            @AuthenticationPrincipal User user
    ) {
        Address savedAddress = addressService.updateAddress(
                id,
                updatedAddress,
                user.getId()
        );

        return ResponseEntity.ok(savedAddress);
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<Address> setDefaultAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        Address updatedAddress =
                addressService.setDefaultAddress(
                        id,
                        user.getId()
                );

        return ResponseEntity.ok(updatedAddress);
    }
}
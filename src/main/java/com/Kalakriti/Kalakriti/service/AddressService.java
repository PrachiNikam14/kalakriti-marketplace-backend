package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.AddressResponseDTO;
import com.Kalakriti.Kalakriti.entity.Address;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AddressResponseDTO addAddress(User user, Address address) {

        address.setUser(user);

        if (address.isDefaultAddress()) {
            Address existingDefault =
                    addressRepository.findByUserAndDefaultAddressTrue(user);

            if (existingDefault != null) {
                existingDefault.setDefaultAddress(false);
                addressRepository.save(existingDefault);
            }
        }

        Address saved = addressRepository.save(address);

        return new AddressResponseDTO(
                saved.getId(),
                saved.getFullName(),
                saved.getPhoneNumber(),
                saved.getLine1(),
                saved.getLine2(),
                saved.getCity(),
                saved.getState(),
                saved.getPincode(),
                saved.getCountry(),
                saved.isDefaultAddress(),
                saved.getCreatedAt()
        );
    }

    public List<Address> getUserAddresses(User user) {
        return addressRepository.findByUser(user);
    }

    public void deleteAddress(User user, Long addressId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        addressRepository.delete(address);
    }
}
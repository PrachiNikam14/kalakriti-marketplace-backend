package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.dto.AddressResponseDTO;
import com.Kalakriti.Kalakriti.entity.Address;
import com.Kalakriti.Kalakriti.entity.User;
import com.Kalakriti.Kalakriti.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    // ==============================
    // ADD ADDRESS
    // ==============================
    @Transactional
    public AddressResponseDTO addAddress(
            User user,
            Address address
    ) {
        address.setId(null);
        address.setUser(user);

        if (address.isDefaultAddress()) {
            unsetExistingDefaultAddress(user);
        }

        Address savedAddress = addressRepository.save(address);

        return convertToDTO(savedAddress);
    }

    // ==============================
    // GET USER ADDRESSES
    // ==============================
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getUserAddressDTOs(User user) {
        return addressRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ==============================
    // DELETE ADDRESS
    // ==============================
    @Transactional
    public void deleteAddress(
            User user,
            Long addressId
    ) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() ->
                        new RuntimeException("Address not found")
                );

        validateOwnership(address, user.getId());

        addressRepository.delete(address);
    }

    // ==============================
    // UPDATE ADDRESS
    // ==============================
    @Transactional
    public Address updateAddress(
            Long addressId,
            Address updatedAddress,
            Long userId
    ) {
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() ->
                        new RuntimeException("Address not found")
                );

        validateOwnership(existingAddress, userId);

        existingAddress.setFullName(updatedAddress.getFullName());
        existingAddress.setPhoneNumber(updatedAddress.getPhoneNumber());
        existingAddress.setLine1(updatedAddress.getLine1());
        existingAddress.setLine2(updatedAddress.getLine2());
        existingAddress.setCity(updatedAddress.getCity());
        existingAddress.setState(updatedAddress.getState());
        existingAddress.setPincode(updatedAddress.getPincode());
        existingAddress.setCountry(updatedAddress.getCountry());

        if (updatedAddress.isDefaultAddress()) {
            unsetExistingDefaultAddress(existingAddress.getUser());
            existingAddress.setDefaultAddress(true);
        } else {
            existingAddress.setDefaultAddress(false);
        }

        return addressRepository.save(existingAddress);
    }

    // ==============================
    // SET DEFAULT ADDRESS
    // ==============================
    @Transactional
    public Address setDefaultAddress(
            Long addressId,
            Long userId
    ) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() ->
                        new RuntimeException("Address not found")
                );

        validateOwnership(address, userId);

        unsetExistingDefaultAddress(address.getUser());

        address.setDefaultAddress(true);

        return addressRepository.save(address);
    }

    // ==============================
    // REMOVE EXISTING DEFAULT
    // ==============================
    private void unsetExistingDefaultAddress(User user) {
        List<Address> userAddresses =
                addressRepository.findByUserId(user.getId());

        for (Address userAddress : userAddresses) {
            userAddress.setDefaultAddress(false);
        }

        addressRepository.saveAll(userAddresses);
    }

    // ==============================
    // OWNERSHIP VALIDATION
    // ==============================
    private void validateOwnership(
            Address address,
            Long userId
    ) {
        if (address.getUser() == null
                || !address.getUser().getId().equals(userId)) {

            throw new RuntimeException("Unauthorized access");
        }
    }

    // ==============================
    // ENTITY TO DTO
    // ==============================
    private AddressResponseDTO convertToDTO(Address address) {
        return new AddressResponseDTO(
                address.getId(),
                address.getFullName(),
                address.getPhoneNumber(),
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getState(),
                address.getPincode(),
                address.getCountry(),
                address.isDefaultAddress(),
                address.getCreatedAt()
        );
    }
}
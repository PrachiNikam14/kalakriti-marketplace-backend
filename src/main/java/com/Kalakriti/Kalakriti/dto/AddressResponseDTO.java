package com.Kalakriti.Kalakriti.dto;

import java.time.LocalDateTime;

public class AddressResponseDTO {

    private Long id;
    private String fullName;
    private String phoneNumber;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private boolean defaultAddress;
    private LocalDateTime createdAt;

    public AddressResponseDTO(Long id,
                              String fullName,
                              String phoneNumber,
                              String line1,
                              String line2,
                              String city,
                              String state,
                              String pincode,
                              String country,
                              boolean defaultAddress,
                              LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.country = country;
        this.defaultAddress = defaultAddress;
        this.createdAt = createdAt;
    }

    // getters only
    public AddressResponseDTO() {
        super();

    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getLine1() {
        return line1;

    }
    public void setLine1(String line1) {
        this.line1 = line1;
    }
    public String getLine2() {
        return line2;
    }
    public void setLine2(String line2) {
        this.line2 = line2;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getPincode() {
        return pincode;
    }
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public boolean isDefaultAddress() {
        return defaultAddress;
    }
    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;

    }
    public LocalDateTime getCreatedAt() {
        return createdAt;

    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
package com.Kalakriti.Kalakriti.dto;

import com.Kalakriti.Kalakriti.entity.Role;

public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;

    public UserProfileResponse() {
    }

    public UserProfileResponse(
            Long id,
            String name,
            String email,
            String phoneNumber,
            Role role) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Role getRole() {
        return role;
    }
}
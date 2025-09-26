package com.joon.sunguard_api.domain.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String role;
    private String name;
    private String username;
}

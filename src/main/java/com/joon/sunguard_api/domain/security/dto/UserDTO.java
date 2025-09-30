package com.joon.sunguard_api.domain.security.dto;

import com.joon.sunguard_api.domain.security.util.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Role role;
    private String name;
    private String username;
}

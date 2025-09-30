package com.joon.sunguard_api.domain.security.dto;

import com.joon.sunguard_api.domain.security.util.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class UserDTO {
    private Role role;
    private String name;
    private String username;

    @Builder
    public UserDTO(Role role, String name, String username){
        this.role = role;
        this.name = name;
        this.username = username;
    }
}

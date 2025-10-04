package com.joon.sunguard_api.domain.security.entity;

import com.joon.sunguard_api.domain.security.util.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_entity")
@Getter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String name;
    private String email;

    //TODO : role을 enum 타입으로 변경
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public UserEntity(String username, String name, String email, Role role){
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public void updateUserEntity(String name, String email){
        this.name = name;
        this.email = email;
    }

}
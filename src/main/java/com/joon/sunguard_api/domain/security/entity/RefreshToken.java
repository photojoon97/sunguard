package com.joon.sunguard_api.domain.security.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    private String refreshToken; // 해시된 값 저장

    @Column
    private LocalDateTime issuedAt;

    @Column
    private LocalDateTime expiredAt;

    @Column
    private LocalDateTime lastUsedAt; // 마지막 사용 일시

    /*
    //동시 로그인 제한용 데이터
    @Column(nullable = false)
    private String ipAddress; // 기기 식별용 IP 주소

    @Column(nullable = false, length = 512)
    private String userAgent; // 기기 식별용 User-Agent
    */

    @Builder
    public RefreshToken(UserEntity user, String refreshToken, LocalDateTime expiredAt){
        this.user = user;
        this.refreshToken = refreshToken;
        this.issuedAt = LocalDateTime.now();
        this.expiredAt = expiredAt;
    }

    public void udpateLastUsedAt(){
        this.lastUsedAt = LocalDateTime.now();
    }

}

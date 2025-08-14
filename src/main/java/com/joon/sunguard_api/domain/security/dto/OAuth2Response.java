package com.joon.sunguard_api.domain.security.dto;

public interface OAuth2Response {
    String getProvider(); // Provider (github, google)

    String getProviderId(); // Provider에서 제공하는 ID

    String getEmail();

    String getName(); //사용자 이름
}

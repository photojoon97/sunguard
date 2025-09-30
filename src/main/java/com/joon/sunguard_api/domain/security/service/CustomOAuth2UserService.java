package com.joon.sunguard_api.domain.security.service;


import com.joon.sunguard_api.domain.security.dto.CustomOAuth2User;
import com.joon.sunguard_api.domain.security.dto.GithubResponse;
import com.joon.sunguard_api.domain.security.dto.OAuth2Response;
import com.joon.sunguard_api.domain.security.dto.UserDTO;
import com.joon.sunguard_api.domain.security.entity.UserEntity;
import com.joon.sunguard_api.domain.security.repository.UserRepository;
import com.joon.sunguard_api.domain.security.util.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //인증서버에서 받아온 Access Token을 userRequest에 담음
        OAuth2User oAuth2User = super.loadUser(userRequest); //userRequest에로 리소스 서버에서 사용자 정보 객체 oAuth2User 가져옴
        Object login = oAuth2User.getAttributes().get("login");
        Object id = oAuth2User.getAttributes().get("id");
        Object grantedAuthorities = oAuth2User.getAttributes().get("Granted Authorities");
        Object email = oAuth2User.getAttributes().get("email");

        log.info("oAuth2User : {}  id : {}  Granted Authorities : {}    email : {}", login, id, grantedAuthorities, email);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response;

        if (registrationId.equals("github")) {
            oAuth2Response = new GithubResponse(oAuth2User.getAttributes());
        } else {
            log.error("Unsupported provider: {}", registrationId);
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        // Optional로 받고, orElseGet을 사용하여 신규/기존 사용자 처리
        UserEntity userEntity = userRepository.findByUsername(username)
                .map(existingUser -> {
                    existingUser.updateUserEntity(oAuth2Response.getName(), oAuth2Response.getEmail());
                    return existingUser;
                })
                .orElseGet(() -> UserEntity.builder() //신규 사용자 생성
                        .username(username)
                        .name(oAuth2Response.getName())
                        .email(oAuth2Response.getEmail())
                        .role(Role.USER)
                        .build()
                ); //기존 사용자여도 메서드 체인으로 orElseGet을 거치면서 최종적으로 UserEntity 뱉음 (내부 람다식은 실행되지 않음)

        userRepository.save(userEntity);

        UserDTO userDto = new UserDTO();
        userDto.setUsername(userEntity.getUsername());
        userDto.setName(userEntity.getName());
        userDto.setRole(userEntity.getRole());

        return new CustomOAuth2User(userDto);

    }
}

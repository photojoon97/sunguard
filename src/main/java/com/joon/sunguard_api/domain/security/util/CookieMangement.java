package com.joon.sunguard_api.domain.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieMangement {

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60 * 7);
        // cookie.setSecure(true); // For HTTPS
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    public void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null); // value를 null로 설정
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 즉시 삭제
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    public String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

}

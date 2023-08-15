package com.devresume.application.security.oauth2;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.devresume.application.security.AuthenticatedUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private AuthenticatedUser authenticatedUser;

    public OAuth2AuthenticationSuccessHandler(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Xử lý sau khi đăng nhập thành công
        // authenticatedUser.handleAuthentication(authentication);

        // Chuyển hướng về trang chính hoặc trang khác
        response.sendRedirect("/"); // Thay bằng URL của trang bạn muốn chuyển hướng
    }
}


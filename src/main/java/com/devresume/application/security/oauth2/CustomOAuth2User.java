package com.devresume.application.security.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.devresume.application.data.OAuth2ProviderType;

public class CustomOAuth2User implements OAuth2User {

    private OAuth2User oAuth2User;
    private String oAuth2ClientName;

    public CustomOAuth2User(OAuth2User oAuth2User, String oAuth2ClientName) {
        this.oAuth2User = oAuth2User;
        this.oAuth2ClientName = oAuth2ClientName;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getAttribute("name");
    }

    public String getOauth2ClientName() {
        return this.oAuth2ClientName;
    }

    public String getUID() {
        if (OAuth2ProviderType.GOOGLE.equals(OAuth2ProviderType.valueOf(this.oAuth2ClientName.toUpperCase()))) {
            return oAuth2User.getAttribute("sub");
        } else {
            return oAuth2User.getAttribute("id");
        }
    }

    public String getUsername() {
        return oAuth2User.getAttribute("email");
    }

    public String getFirstName() {
        return oAuth2User.getAttribute("family_name");
    }

    public String getLastName() {
        return oAuth2User.getAttribute("given_name");
    }

    public String getLangKey() {
        return oAuth2User.getAttribute("locale");
    }

    public String getImageUrl() {
        if (OAuth2ProviderType.GOOGLE.equals(OAuth2ProviderType.valueOf(this.oAuth2ClientName.toUpperCase()))) {
            return oAuth2User.getAttribute("picture");
        } else {
            return "https://graph.facebook.com/" + getUID() + "/picture?type=square";
        }
    }
}

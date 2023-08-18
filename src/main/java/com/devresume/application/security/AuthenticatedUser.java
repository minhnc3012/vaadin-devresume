package com.devresume.application.security;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.devresume.application.data.AuthoritiesConstants;
import com.devresume.application.data.OAuth2ProviderType;
import com.devresume.application.entity.Authority;
import com.devresume.application.entity.User;
import com.devresume.application.repository.UserRepository;
import com.devresume.application.security.oauth2.CustomOAuth2User;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    @Transactional
    public Optional<User> get() {
    	SecurityContext securityContext = SecurityContextHolder.getContext();
    	Authentication authentication = securityContext.getAuthentication();
    	if(authentication.getPrincipal() instanceof CustomOAuth2User) {
    		return authenticationContext.getAuthenticatedUser(CustomOAuth2User.class)
                    .map(oAuth2User -> {
                    	User user = new User();
                        user.setEmail(oAuth2User.getUsername());
                        user.setProvider(OAuth2ProviderType.valueOf(oAuth2User.getOauth2ClientName().toUpperCase()));
                        user.setProviderUID(oAuth2User.getUID());
                        if (OAuth2ProviderType.FACEBOOK.equals(user.getProvider())) {
                            String firstName = oAuth2User.getName().substring(0, oAuth2User.getName().indexOf(" ")).trim();
                            String lastName = oAuth2User.getName().substring(oAuth2User.getName().indexOf(" "), oAuth2User.getName().length()).trim();
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                        } else {
                            user.setFirstName(oAuth2User.getFirstName());
                            user.setLastName(oAuth2User.getLastName());
                        }
                        user.setLangKey(oAuth2User.getLangKey());
                        user.setImageUrl(oAuth2User.getImageUrl());
                        user.setUsername(oAuth2User.getUsername());
                        user.setActivated(Boolean.TRUE);

                        Set<Authority> authorities = new HashSet<>();
                        authorities.add(new Authority().name(AuthoritiesConstants.USER));
                        user.setAuthorities(authorities);
                        
                        return user;
                    });
    	} else {
    		return authenticationContext.getAuthenticatedUser(UserDetails.class)
                    .map(userDetails -> userRepository.findOneByUsernameIgnoreCase(userDetails.getUsername()).get());
    	}
        
    }

    public void logout() {
        authenticationContext.logout();
    }

}

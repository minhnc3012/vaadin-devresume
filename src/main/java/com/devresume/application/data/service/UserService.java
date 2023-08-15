package com.devresume.application.data.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.devresume.application.data.AuthoritiesConstants;
import com.devresume.application.data.OAuth2ProviderType;
import com.devresume.application.data.entity.Authority;
import com.devresume.application.data.entity.User;
import com.devresume.application.repository.UserRepository;
import com.devresume.application.security.oauth2.CustomOAuth2User;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    public Optional<User> get(Long id) {
        return userRepository.findById(id);
    }

    public User update(User entity) {
        return userRepository.save(entity);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return userRepository.findAll(filter, pageable);
    }

    public int count() {
        return (int) userRepository.count();
    }

    public void processOAuthPostLogin(CustomOAuth2User oAuth2User) {
        String username = oAuth2User.getUsername();
        Optional<User> optionalUser = userRepository.findOneByUsername(username);
        User user = null;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
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
        } else {
            user = new User();
            user.setEmail(username);
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
            user.setUsername(username);
            user.setActivated(Boolean.TRUE);

            Set<Authority> authorities = new HashSet<>();
            authorities.add(new Authority().name(AuthoritiesConstants.USER));
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
    }
}

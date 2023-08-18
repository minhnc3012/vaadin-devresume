package com.devresume.application.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devresume.application.data.AuthoritiesConstants;
import com.devresume.application.data.OAuth2ProviderType;
import com.devresume.application.entity.Authority;
import com.devresume.application.entity.User;
import com.devresume.application.exception.UsernameAlreadyUsedException;
import com.devresume.application.repository.AuthorityRepository;
import com.devresume.application.repository.UserRepository;
import com.devresume.application.security.oauth2.CustomOAuth2User;
import com.devresume.application.util.RandomUtil;

@Service
@Transactional
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private  AuthorityRepository authorityRepository;
    @Autowired
    private  UserRepository userRepository;

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

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }
    
    public User registerUser(User register) {
        userRepository
            .findOneByUsernameIgnoreCase(register.getUsername().toLowerCase())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new UsernameAlreadyUsedException();
                }
            });
        
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode("123");
        newUser.setUsername(register.getUsername().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(register.getFirstName());
        newUser.setLastName(register.getLastName());
        newUser.setEmail(register.getUsername().toLowerCase());
        
        newUser.setImageUrl("");
        newUser.setLangKey("en");
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        
        return newUser;
    }
    
    public void processOAuthPostLogin(CustomOAuth2User oAuth2User) {
        String username = oAuth2User.getUsername();
        Optional<User> optionalUser = userRepository.findOneByUsernameIgnoreCase(username);
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
    
    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                log.debug("Activated user: {}", user);
                return user;
            });
    }
    
    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
            .findOneByUsernameIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                return user;
            });
    }
    
    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return user;
            });
    }
}

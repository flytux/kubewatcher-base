package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.config.properties.UserProperties;
import com.kubeworks.watcher.data.entity.KwUser;
import com.kubeworks.watcher.user.service.NativeUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Profile("!keycloak")
@Slf4j
@Service
public class NativeUserServiceImpl implements UserDetailsService, NativeUserService {

    private final Map<String, KwUser> users;
    private final PasswordEncoder passwordEncoder;

    public NativeUserServiceImpl(UserProperties userProperties, PasswordEncoder passwordEncoder) {
        this.users = userProperties.getUsers()
            .stream().collect(Collectors.toMap(KwUser::getUsername, user -> user));
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        KwUser user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("not found user // username=" + username);
        }
        return User.withUsername(user.getUsername())
            .passwordEncoder(passwordEncoder::encode)
            .password(user.getPassword())
            .roles(user.getRole())
            .build();
    }

    @Override
    public boolean signUpUser(KwUser user) {
        return false;
    }

}

package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.data.entity.KwUser;
import com.kubeworks.watcher.data.entity.KwUserRole;
import com.kubeworks.watcher.data.repository.KwUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j @Service
public class NativeUserServiceImpl implements UserDetailsService {

    private final PasswordEncoder encoder;
    private final KwUserRepository repository;

    @Autowired
    public NativeUserServiceImpl(final PasswordEncoder encoder, final KwUserRepository repository) {
        this.encoder = encoder; this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        return repository.findById(username).map(this::createUserDetails).orElseThrow(() -> new UsernameNotFoundException("Username not found -> " + username));
    }

    private UserDetails createUserDetails(final KwUser user) {
        return User.withUsername(user.getUsername()).passwordEncoder(encoder::encode).password(user.getPassword()).roles(reformat(user)).build();
    }

    private String[] reformat(final KwUser user) {

        final List<KwUserRole> sources = user.getRole();
        final String[] res = new String[sources.size()];

        for (int n=0; n<sources.size(); n++) {
            res[n] = sources.get(n).getRolename().getRolename();
        }

        return res;
    }
}

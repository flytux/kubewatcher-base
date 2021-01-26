package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.config.properties.UserProperties;
import com.kubeworks.watcher.data.entity.KwUser;
import com.kubeworks.watcher.data.entity.KwUserRole;
import com.kubeworks.watcher.data.entity.KwUserRoleId;
import com.kubeworks.watcher.data.repository.KwUserRepository;
import com.kubeworks.watcher.user.service.NativeUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;


@Slf4j
@Service("userDetailsService")
public class NativeUserServiceImpl implements UserDetailsService, NativeUserService {

    private final PasswordEncoder passwordEncoder;
    private final KwUserRepository kwUserRepository;

    public NativeUserServiceImpl(PasswordEncoder passwordEncoder, KwUserRepository kwUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.kwUserRepository = kwUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        //유저 로그인 패스를 DB 로 변경, 스프링 시큐리티 콜백함수 유지 (loadUserByUsername)
        Optional<KwUser> optional = kwUserRepository.findById(username);
        KwUser user = optional.get();

        //유저 vs Role (1:n)
        String [] roles = new String[user.getRole().size()];

        int count = 0;
        for (KwUserRole role : user.getRole()){
            KwUserRoleId roleId = role.getRolename();
            roles[count] = roleId.getRolename();
            count++;
        }
        if (user == null) {
            throw new UsernameNotFoundException("not found user // username=" + username);
        }

        //스프링 시큐리티 콜백함수 유지 (to-do : 패스워드 암호화)
        return User.withUsername(user.getUsername())
            //.passwordEncoder(passwordEncoder::encode)
            .password(user.getPassword())
            .roles(roles)
            .build();
    }

    @Override
    public boolean signUpUser(KwUser user) {
        return false;
    }

}

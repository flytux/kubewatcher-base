package com.kubeworks.watcher.user.service;


import com.kubeworks.watcher.data.entity.KwUser;
import org.springframework.security.core.userdetails.UserDetails;

public interface NativeUserService {
    UserDetails loadUserByUsername(String username);
    boolean signUpUser(KwUser user);

}

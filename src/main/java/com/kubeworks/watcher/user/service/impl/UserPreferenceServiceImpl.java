package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.KwUser;
import com.kubeworks.watcher.data.repository.KwUserRepository;
import com.kubeworks.watcher.user.service.UserPreferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final KwUserRepository kwUserRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern PASSWORD_PATTEN = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\p{Punct}+.*)[\\p{Graph}]{8,10}$");

    @Autowired
    public UserPreferenceServiceImpl(KwUserRepository kwUserRepository, PasswordEncoder passwordEncoder) {
        this.kwUserRepository = kwUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApiResponse<String> passwordSave(KwUser kwUser, String newPassword) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            Optional<KwUser> dbUserOptional = kwUserRepository.findById(kwUser.getUsername());
            KwUser dbUser = dbUserOptional.orElseThrow(() -> new IllegalArgumentException("user not found // username=" + kwUser.getUsername()));

            if(kwUser.getPassword() != null && !"".equals(kwUser.getPassword()) && passwordEncoder.matches(kwUser.getPassword(), dbUser.getPassword())){
                if(newPassword != null && !"".equals(newPassword) && PASSWORD_PATTEN.matcher(newPassword).matches()){
                    dbUser.setPassword(passwordEncoder.encode(newPassword));
                    kwUserRepository.save(dbUser);
                    response.setSuccess(true);
                    return response;
                } else {
                    response.setMessage("비밀번호는 8~10자, 영문,숫자,특수문자 각각 1개이상 포함 되어야 합니다.");
                }
            } else {
                response.setMessage("기존비밀번호를 확인해주세요.");
            }
        } catch (Exception e) {
            log.error("비밀번호 업데이트 실패 // username={}", kwUser.getUsername());
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        response.setSuccess(false);
        return response;
    }
}

package com.kubeworks.watcher.user.controller;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.KwUser;
import com.kubeworks.watcher.user.service.KwUserService;
import com.kubeworks.watcher.user.service.UserPreferenceService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;
    private final KwUserService kwUserService;

    @GetMapping(value = "/setting/preference")
    public String preference(Model model){
        model.addAttribute("user", kwUserService.getKwUser(getUser().getUsername()));
        return "setting/preference/preference";
    }

    @ResponseBody
    @PostMapping(value = "/setting/preference/passwordSave")
    public ApiResponse<String> passwordSave(@ModelAttribute("user") KwUser kwUser, @RequestParam("newPassword") String newPassword){
        return userPreferenceService.passwordSave(kwUser, newPassword);
    }


    protected User getUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}

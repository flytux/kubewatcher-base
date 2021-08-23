package com.kubeworks.watcher.user.service;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.KwUser;

public interface UserPreferenceService {

    ApiResponse<String> passwordSave(KwUser kwUser, String newPassword);

}

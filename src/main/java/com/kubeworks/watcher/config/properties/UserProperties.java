package com.kubeworks.watcher.config.properties;

import com.kubeworks.watcher.data.entity.KwUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter @Setter
@ConfigurationProperties(prefix = "application.properties.auth")
public class UserProperties {
    List<KwUser> users;
}

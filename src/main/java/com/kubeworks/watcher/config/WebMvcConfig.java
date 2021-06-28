package com.kubeworks.watcher.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(@NonNull final FormatterRegistry registry) {

        final DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true); registrar.registerFormatters(registry);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/*").allowedOrigins("*");
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {

        registry.addViewController("/").setViewName("redirect:/main");

        // 이 매핑들은 사용되지 않는걸로 판단됨
        registry.addViewController("/monitoring/application/topologies").setViewName("monitoring/application/topologies");
        registry.addViewController("/monitoring/alarm/list").setViewName("monitoring/alarm/alarm-list");
        registry.addViewController("/application/catalog/applications").setViewName("application/catalog/application-list");
        registry.addViewController("/application/catalog/releases").setViewName("application/catalog/release-list");
        registry.addViewController("/security/keys").setViewName("security/keys");
        registry.addViewController("/ncp/audit").setViewName("ncp/audit");
        registry.addViewController("/ncp/authorization").setViewName("ncp/authorization");
        registry.addViewController("/ncp/groups").setViewName("ncp/groups");
        registry.addViewController("/ncp/policies").setViewName("ncp/policies");
        registry.addViewController("/ncp/roles").setViewName("ncp/roles");
        registry.addViewController("/ncp/users").setViewName("ncp/users");

        registry.addViewController("/setting/preference").setViewName("setting/preference/preference");
    }
}

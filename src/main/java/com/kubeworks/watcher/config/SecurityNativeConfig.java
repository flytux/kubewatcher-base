package com.kubeworks.watcher.config;

import com.kubeworks.watcher.user.handler.KwUserLoginSuccessHandler;
import com.kubeworks.watcher.user.security.KwUserAuthenticationFilter;
import com.kubeworks.watcher.user.security.KwUserAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SecurityNativeConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService nativeUserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/assets/**", "/vendor/bootstrap/**", "/css/**", "/webssh/**", "/h2-console/**").permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/main")
            .permitAll()
            .and()
            .addFilterBefore(kwUserAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .httpBasic().and()
            .csrf().ignoringAntMatchers("/h2-console/**")
            .and()
            .headers()
            .addHeaderWriter(
                new XFrameOptionsHeaderWriter(
                    new WhiteListedAllowFromStrategy(Collections.singletonList("localhost"))
                )
            ).frameOptions().sameOrigin()
            .and()
            .logout()
            .invalidateHttpSession(true)
//            .logoutSuccessHandler(logoutSuccessHandler)
            .permitAll();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(kwUserAuthenticationProvider());
    }

    @Bean
    public KwUserAuthenticationProvider kwUserAuthenticationProvider() {
        KwUserAuthenticationProvider provider = new KwUserAuthenticationProvider(bCryptPasswordEncoder());
        provider.setUserDetailsService(nativeUserService);
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public KwUserAuthenticationFilter kwUserAuthenticationFilter() throws Exception {
        KwUserAuthenticationFilter kwUserAuthenticationFilter = new KwUserAuthenticationFilter(authenticationManager());
//        kwUserAuthenticationFilter.setFilterProcessesUrl("/login");
        kwUserAuthenticationFilter.setAuthenticationSuccessHandler(kwUserLoginSuccessHandler());
        kwUserAuthenticationFilter.afterPropertiesSet();
        return kwUserAuthenticationFilter;
    }

    @Bean
    public KwUserLoginSuccessHandler kwUserLoginSuccessHandler() {
        return new KwUserLoginSuccessHandler();
    }

    @Configuration
    public static class PasswordEncoderConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

}


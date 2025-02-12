package com.manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${login.id}")
    String loginId;
    @Value("${login.password}")
    String loginPassword;
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username(loginId)
                .password(loginPassword)
                .build();

        return new MapReactiveUserDetailsService(admin);
    }
}

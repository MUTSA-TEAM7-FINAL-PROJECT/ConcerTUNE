package com.team7.ConcerTUNE.config;

import com.team7.ConcerTUNE.entity.AuthProvider;
import com.team7.ConcerTUNE.entity.AuthRole;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserInitializer {
    private static final String DEFAULT_EMAIL = "user@naver.com";
    private static final String DEFAULT_PASSWORD = "password12";
    private static final String DEFAULT_USERNAME = "Initial User";

    @Bean
    public CommandLineRunner initDefaultUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("--- 초기 유저 설정 ---");
            String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);

            User defaultUser = User.builder()
                    .email(DEFAULT_EMAIL)
                    .password(encodedPassword)
                    .username(DEFAULT_USERNAME)
                    .auth(AuthRole.USER)
                    .provider(AuthProvider.LOCAL)
                    .enabled(true)
                    .build();

            userRepository.save(defaultUser);

        };
    }
}
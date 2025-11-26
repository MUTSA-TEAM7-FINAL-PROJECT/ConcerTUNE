package com.team7.ConcerTUNE.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "toss.payments")
@Data
public class TossPaymentsConfig {
    private String clientKey;
    private String secretKey;
    private Api api = new Api();

    @Data
    public static class Api {
        private String baseUrl;
    }
}
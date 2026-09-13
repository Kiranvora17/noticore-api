package com.noticore.noticore_api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
public class BrevoConfig {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.webhook.secret}")
    private String webhookSecret;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

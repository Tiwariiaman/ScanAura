package com.scanaura.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Bean
    public RestClient geminiRestClient() {

        return RestClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

}
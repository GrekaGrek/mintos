package net.mint.java.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${exchange-rates.api}")
    private String exchangeRatesApi;

    @Bean
    public WebClient webClient() {
        return WebClient.create(exchangeRatesApi);
    }
}

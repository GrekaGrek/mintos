package net.mint.java.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public Mono<Map<String, BigDecimal>> fetchExchangeRates(String currency) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{currency}")
                        .build(currency.toUpperCase()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(String.class)
                .map(this::extractConversionRates)
                .retryWhen(handleRetry());
    }

    Map<String, BigDecimal> extractConversionRates(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode ratesNode = root.get("conversion_rates");
            return objectMapper.convertValue(ratesNode, new TypeReference<Map<String, BigDecimal>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    Mono<Throwable> handleError(ClientResponse clientResponse) {
        return clientResponse.createException()
                .flatMap(Mono::error);
    }

    private Retry handleRetry() {
        return Retry.backoff(3, Duration.ofSeconds(1))
                .filter(ex -> ex instanceof WebClientResponseException)
                .onRetryExhaustedThrow(((retryBackoffSpec, signal) ->
                        new Exception("Failed to call ExchangeRate Api after 3 attempts", signal.failure())));
    }
}

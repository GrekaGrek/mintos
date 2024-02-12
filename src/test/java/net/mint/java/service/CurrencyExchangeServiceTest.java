package net.mint.java.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyExchangeServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CurrencyExchangeService currencyExchangeService;

    @Test
    void testFetchExchangeRatesErrorResponse() {
        WebClientResponseException exception =
                WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null);
        Mono<String> errorMono = Mono.error(exception);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(errorMono);

        Mono<Map<String, BigDecimal>> result = currencyExchangeService.fetchExchangeRates("USD");

        StepVerifier.create(result)
                .expectErrorMessage("Failed to call ExchangeRate Api after 3 attempts")
                .verify();

        verify(webClient).get();
    }

    @Test
    void testHandleRetry() {
        ClientResponse clientResponse = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build();

        Mono<Throwable> result = currencyExchangeService.handleError(clientResponse);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof WebClientResponseException
                                && ((WebClientResponseException) throwable).getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verify();
    }
}
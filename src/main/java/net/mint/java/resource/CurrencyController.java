package net.mint.java.resource;

import lombok.RequiredArgsConstructor;
import net.mint.java.service.CurrencyExchangeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

/**
 - This Controller was made only for purpose to check connection with
 <a href="https://v6.exchangerate-api.com/v6/6cc3efb2aca092b077fe0ffe/latest/{currency}"/>
- If you don't receive response, please visit
 <a href="https://v6.exchangerate-api.com" /> to get a key and replace it in application.yml.
 */

@RestController
@RequestMapping("/v1/currency-rates")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyExchangeService exchangeService;

    @GetMapping("/{currency}")
    public Mono<Map<String, BigDecimal>> getExchangeRate(@PathVariable String currency) {
        return exchangeService.fetchExchangeRates(currency);
    }
}

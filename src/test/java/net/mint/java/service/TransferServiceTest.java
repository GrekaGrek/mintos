package net.mint.java.service;

import net.mint.java.domain.AccountEntity;
import net.mint.java.domain.TransferEntity;
import net.mint.java.mapper.AccountMapper;
import net.mint.java.mapper.TransferMapper;
import net.mint.java.model.AccountDTO;
import net.mint.java.model.TransferDTO;
import net.mint.java.repository.AccountRepository;
import net.mint.java.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CurrencyExchangeService exchangeService;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private TransferMapper transferMapper;
    @Mock
    private TransferRepository transferRepository;
    @InjectMocks
    private TransferService transferService;

    @Test
    void transferFundsSuccessfully() {
        var fromAccountId = 1L;
        var toAccountId = 2L;
        var amount = BigDecimal.valueOf(100);
        var currency = "USD";

        var sourceAccount = new AccountEntity();
        sourceAccount.setId(fromAccountId);
        sourceAccount.setBalance(BigDecimal.valueOf(500));
        sourceAccount.setCurrency("USD");

        var targetAccount = new AccountEntity();
        targetAccount.setId(toAccountId);
        targetAccount.setBalance(BigDecimal.valueOf(200));
        targetAccount.setCurrency("EUR");

        var transfer = new TransferEntity();
        transfer.setSourceAccount(sourceAccount);
        transfer.setTargetAccount(targetAccount);

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(targetAccount));

        Map<String, BigDecimal> exchangeRates = Map.of("USD", BigDecimal.valueOf(1.0), "EUR", BigDecimal.valueOf(0.82));
        when(exchangeService.fetchExchangeRates(sourceAccount.getCurrency().toUpperCase()))
                .thenReturn(Mono.just(exchangeRates));

        var accountDTO = AccountDTO.builder().balance(BigDecimal.valueOf(98.00)).build();
        when(accountMapper.mapFrom(any(AccountEntity.class))).thenReturn(accountDTO);

        when(transferMapper.mapFrom(any(TransferDTO.class))).thenReturn(transfer);

        TransferDTO result = transferService.transferFunds(fromAccountId, toAccountId, amount, currency);

        assertNotNull(result);

        verify(accountRepository, times(2)).findById(anyLong());
        verify(accountRepository, times(2)).save(any(AccountEntity.class));
        verify(transferRepository).save(any(TransferEntity.class));
        verify(exchangeService).fetchExchangeRates(sourceAccount.getCurrency().toUpperCase());
        verify(transferMapper).mapFrom(any(TransferDTO.class));
    }

    @Test
    void transferFundsInsufficientFunds() {
        var fromAccountId = 1L;
        var toAccountId = 2L;
        var amount = BigDecimal.valueOf(100);
        var currency = "USD";

        var sourceAccount = new AccountEntity();
        sourceAccount.setBalance(BigDecimal.valueOf(50));

        var targetAccount = new AccountEntity();
        targetAccount.setBalance(BigDecimal.valueOf(50));

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(targetAccount));

        assertThatThrownBy(
                () -> transferService.transferFunds(fromAccountId, toAccountId, amount, currency))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient funds in the source account");

        verify(accountRepository).findById(fromAccountId);
        verify(accountRepository).findById(toAccountId);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void transferFundsMismatchedCurrencies() {
        var fromAccountId = 1L;
        var toAccountId = 2L;
        var amount = BigDecimal.valueOf(100);
        var currency = "USD";

        var sourceAccount = new AccountEntity();
        sourceAccount.setCurrency("EUR");
        sourceAccount.setBalance(BigDecimal.valueOf(150));

        var targetAccount = new AccountEntity();
        targetAccount.setCurrency("GBP");
        targetAccount.setBalance(BigDecimal.valueOf(250));

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(targetAccount));

        assertThatThrownBy(
                () -> transferService.transferFunds(fromAccountId, toAccountId, amount, currency))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Currency of funds must match the currency of the source account");

        verify(accountRepository).findById(fromAccountId);
        verify(accountRepository).findById(toAccountId);
        verifyNoMoreInteractions(accountRepository);
    }
}
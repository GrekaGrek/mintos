package net.mint.java.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.mint.java.domain.AccountEntity;
import net.mint.java.domain.TransferEntity;
import net.mint.java.mapper.AccountMapper;
import net.mint.java.mapper.TransferMapper;
import net.mint.java.model.AccountDTO;
import net.mint.java.model.TransferDTO;
import net.mint.java.repository.AccountRepository;
import net.mint.java.repository.TransferRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final CurrencyExchangeService exchangeService;
    private final AccountMapper accountMapper;
    private final TransferMapper transferMapper;

    @Transactional
    public TransferDTO transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount, String currency) {
        AccountEntity sourceAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Source account not found"));

        AccountEntity targetAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Target account not found"));

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in the source account");
        }
        if (!sourceAccount.getCurrency().equalsIgnoreCase(currency)
                && !targetAccount.getCurrency().equalsIgnoreCase(currency)) {
            throw new IllegalArgumentException("Currency of funds must match the currency of the source account");
        }

        Mono<BigDecimal> exchangeRateMono = exchangeService.fetchExchangeRates(sourceAccount.getCurrency().toUpperCase())
                .map(rates -> rates.get(targetAccount.getCurrency().toUpperCase()));

        BigDecimal convertedAmount = amount.multiply(exchangeRateMono.block());

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        targetAccount.setBalance(targetAccount.getBalance().add(convertedAmount));

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        AccountDTO updatedSourceAccount = accountMapper.mapFrom(sourceAccount);
        AccountDTO updatedTargetAccount = accountMapper.mapFrom(targetAccount);

        TransferDTO transferDTO = TransferDTO.builder()
                .amount(amount)
                .sourceAccount(updatedSourceAccount)
                .targetAccount(updatedTargetAccount)
                .dateTime(LocalDateTime.now())
                .build();
        TransferEntity transferEntity = transferMapper.mapFrom(transferDTO);
        transferRepository.save(transferEntity);

        return transferDTO;
    }
}

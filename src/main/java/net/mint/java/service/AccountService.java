package net.mint.java.service;

import lombok.RequiredArgsConstructor;
import net.mint.java.domain.AccountEntity;
import net.mint.java.mapper.AccountMapper;
import net.mint.java.model.AccountDTO;
import net.mint.java.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public List<AccountDTO> getAccountsByCustomerId(Long id) {
        List<AccountEntity> accounts = accountRepository.findByCustomerId(id);
        return accounts.stream()
                .map(accountMapper::mapFrom)
                .toList();
    }
}

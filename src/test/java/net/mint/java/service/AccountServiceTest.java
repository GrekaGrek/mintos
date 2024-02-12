package net.mint.java.service;

import net.mint.java.domain.AccountEntity;
import net.mint.java.mapper.AccountMapper;
import net.mint.java.model.AccountDTO;
import net.mint.java.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;


    @Test
    void testGetAccountsByCustomerIdWhenNoAccountsFound() {
        Long customerId = 1L;
        when(accountRepository.findByCustomerId(customerId)).thenReturn(new ArrayList<>());

        List<AccountDTO> result = accountService.getAccountsByCustomerId(customerId);

        assertEquals(0, result.size());

        verify(accountRepository).findByCustomerId(customerId);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void testGetAccountsByCustomerIdWhenAccountsFound() {
        Long customerId = 1L;
        List<AccountEntity> accountEntities = List.of(createMockAccountEntity());
        List<AccountDTO> expectedAccountDTOs = List.of(createMockAccountDTO());

        when(accountRepository.findByCustomerId(customerId)).thenReturn(accountEntities);
        when(accountMapper.mapFrom(any(AccountEntity.class))).thenReturn(createMockAccountDTO());

        List<AccountDTO> result = accountService.getAccountsByCustomerId(customerId);

        assertEquals(expectedAccountDTOs.size(), result.size());
        assertEquals(expectedAccountDTOs.get(0), result.get(0));

        verify(accountRepository).findByCustomerId(customerId);
        verify(accountMapper, times(accountEntities.size())).mapFrom(any(AccountEntity.class));
    }

    private AccountEntity createMockAccountEntity() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setCurrency("USD");
        accountEntity.setBalance(BigDecimal.valueOf(1000));
        return accountEntity;
    }

    private AccountDTO createMockAccountDTO() {
        return new AccountDTO(1L, "USD", BigDecimal.valueOf(1000), new ArrayList<>());
    }
}
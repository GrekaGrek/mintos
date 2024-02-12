package net.mint.java.resource;

import net.mint.java.GlobalExceptionHandler;
import net.mint.java.model.AccountDTO;
import net.mint.java.model.TransferDTO;
import net.mint.java.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    private static final String API_URL = "/v1/transfers/{fromAccountId}/{toAccountId}";

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testTransferFundsSuccess() throws Exception {
        var fromAccountId = 1L;
        var toAccountId = 2L;
        var amount = BigDecimal.valueOf(100);
        var currency = "USD";
        var expectedTransfer = new TransferDTO(
                amount,
                LocalDateTime.of(2024, 12, 1, 22, 35, 44),
                AccountDTO.builder().build(), AccountDTO.builder().build());

        when(transferService.transferFunds(eq(fromAccountId), eq(toAccountId), eq(amount), eq(currency)))
                .thenReturn(expectedTransfer);

        mockMvc.perform(post(API_URL, fromAccountId, toAccountId)
                        .param("amount", amount.toString())
                        .param("currency", currency))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value("100"))
                .andExpect(jsonPath("$.source_account").value(""));

        verify(transferService).transferFunds(fromAccountId, toAccountId, amount, currency);
    }

    @Test
    void testTransferFundsInvalidCurrency() throws Exception {
        var fromAccountId = 1L;
        var toAccountId = 2L;
        var amount = BigDecimal.valueOf(100);
        var currency = "INVALID";

        when(transferService.transferFunds(fromAccountId, toAccountId, amount, currency))
                .thenThrow(new IllegalArgumentException("Currency of funds must match the currency of the source account"));

        mockMvc.perform(post(API_URL, fromAccountId, toAccountId)
                        .param("amount", amount.toString())
                        .param("currency", currency))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(IllegalArgumentException.class, result.getResolvedException()))
                .andDo(print());

        verify(transferService).transferFunds(fromAccountId, toAccountId, amount, currency);
    }

    @Test
    void testTransferFundsInsufficientFunds() throws Exception {
        var fromAccountId = 1L;
        var toAccountId = 2L;
        var amount = BigDecimal.valueOf(100);
        var currency = "USD";

        when(transferService.transferFunds(eq(fromAccountId), eq(toAccountId), eq(amount), eq(currency)))
                .thenThrow(new IllegalArgumentException("Insufficient funds in the source account"));

        mockMvc.perform(post(API_URL, fromAccountId, toAccountId)
                        .param("amount", amount.toString())
                        .param("currency", currency))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(IllegalArgumentException.class, result.getResolvedException()))
                .andDo(print());

        verify(transferService).transferFunds(fromAccountId, toAccountId, amount, currency);
    }

    @Test
    void testTransferFundsInternalServerError() throws Exception {
        var fromAccountId = 1L;
        var toAccountId = 2L;
        var amount = BigDecimal.valueOf(100);
        var currency = "USD";

        when(transferService.transferFunds(fromAccountId, toAccountId, amount, currency))
                .thenThrow(new RuntimeException("Internal Server Error"));

        mockMvc.perform(post(API_URL, fromAccountId, toAccountId)
                        .param("amount", amount.toString())
                        .param("currency", currency))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertInstanceOf(RuntimeException.class, result.getResolvedException()))
                .andDo(print());

        verify(transferService).transferFunds(fromAccountId, toAccountId, amount, currency);
    }
}
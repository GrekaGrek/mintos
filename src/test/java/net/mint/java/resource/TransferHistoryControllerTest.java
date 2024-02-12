package net.mint.java.resource;

import jakarta.persistence.EntityNotFoundException;
import net.mint.java.GlobalExceptionHandler;
import net.mint.java.model.TransferDTO;
import net.mint.java.service.TransferHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransferHistoryControllerTest {

    private static final String API_URL = "/v1/transfer-history/accounts/{accountId}/transactions";

    @Mock
    private TransferHistoryService transferHistoryService;

    @InjectMocks
    private TransferHistoryController historyController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(historyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testGetTransactionHistorySuccess() throws Exception {
        var accountId = 123L;
        var offset = 0;
        var limit = 10;
        var sortDirection = "DESC";

        List<TransferDTO> transfers = Arrays.asList(
                TransferDTO.builder().build(),
                TransferDTO.builder().build()
        );

        when(transferHistoryService.getTransactionHistory(accountId, offset, limit, sortDirection))
                .thenReturn(transfers);

        mockMvc.perform(get(API_URL, accountId)
                        .param("offset", String.valueOf(offset))
                        .param("limit", String.valueOf(limit))
                        .param("sortDirection", sortDirection))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());

        verify(transferHistoryService).getTransactionHistory(accountId, offset, limit, sortDirection);
    }

    @Test
    public void testGetTransactionHistoryInvalidAccountId() throws Exception {
        var invalidAccountId = 999L;
        var offset = 0;
        var limit = 10;
        var sortDirection = "DESC";

        when(transferHistoryService.getTransactionHistory(invalidAccountId, offset, limit, sortDirection))
                .thenThrow(new EntityNotFoundException("Account not found"));

        mockMvc.perform(get(API_URL, invalidAccountId)
                        .param("offset", String.valueOf(offset))
                        .param("limit", String.valueOf(limit))
                        .param("sortDirection", sortDirection))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(EntityNotFoundException.class, result.getResolvedException()))
                .andDo(print());

        verify(transferHistoryService).getTransactionHistory(invalidAccountId, offset, limit, sortDirection);
    }

    @Test
    public void testGetTransactionHistoryInternalServerError() throws Exception {
        var accountId = 123L;
        var offset = 0;
        var limit = 10;
        var sortDirection = "DESC";

        when(transferHistoryService.getTransactionHistory(accountId, offset, limit, sortDirection))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(get(API_URL, accountId)
                        .param("offset", String.valueOf(offset))
                        .param("limit", String.valueOf(limit))
                        .param("sortDirection", sortDirection))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertInstanceOf(RuntimeException.class, result.getResolvedException()))
                .andDo(print());

        verify(transferHistoryService).getTransactionHistory(accountId, offset, limit, sortDirection);
    }
}
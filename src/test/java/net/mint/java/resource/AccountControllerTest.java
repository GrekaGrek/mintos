package net.mint.java.resource;

import net.mint.java.model.AccountDTO;
import net.mint.java.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private static final String API_URL = "/v1/accounts/{customerId}";

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAccountsByCustomerIdReturnEmptyAccounts() throws Exception {
        var customerId = 1L;

        when(accountService.getAccountsByCustomerId(customerId)).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get(API_URL, customerId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();

        verify(accountService).getAccountsByCustomerId(customerId);
    }

    @Test
    void testGetAccountsByCustomerIdReturnAccounts() throws Exception {
        var customerId = 1L;

        var expectedResponse = List.of(new AccountDTO(customerId, "EUR", valueOf(45.60), List.of()),
                new AccountDTO(customerId, "USD", valueOf(45.60), List.of()));

        when(accountService.getAccountsByCustomerId(customerId)).thenReturn(expectedResponse);

        mockMvc.perform(
                        get(API_URL, customerId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerId").value(1L))
                .andExpect(jsonPath("$[0].currency").value("EUR"))
                .andExpect(jsonPath("$[0].balance").value(45.6))
                .andExpect(jsonPath("$[0].transactions").isArray())
                .andExpect(jsonPath("$[1].customerId").value(1L))
                .andExpect(jsonPath("$[1].currency").value("USD"))
                .andExpect(jsonPath("$[1].balance").value(45.6))
                .andExpect(jsonPath("$[1].transactions").isArray())
                .andReturn();

        verify(accountService).getAccountsByCustomerId(customerId);
    }

    @Test
    void testGetAccountsByCustomerIdReturnNotFound() throws Exception {
        var customerId = 123L;

        mockMvc.perform(
                        get("/wrong-url", customerId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAccountsByCustomerIdReturnBadRequest() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get(API_URL, "null")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }
}
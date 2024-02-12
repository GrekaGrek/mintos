package net.mint.java.service;

import net.mint.java.domain.TransferEntity;
import net.mint.java.mapper.TransferMapper;
import net.mint.java.model.TransferDTO;
import net.mint.java.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferHistoryServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private TransferMapper transferMapper;

    @InjectMocks
    private TransferHistoryService transferHistoryService;

    @Test
    void testGetTransactionHistory() {
        var pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dateTime"));
        Page<TransferEntity> transfersPage = createMockTransfersPage();
        List<TransferEntity> transfers = transfersPage.getContent();
        List<TransferDTO> expectedTransferDTOs = createMockTransferDTOs();

        when(transferRepository.findBySourceAccountIdOrderByDateTimeDesc(1L, pageable)).thenReturn(transfersPage);
        when(transferMapper.mapFrom(any(TransferEntity.class)))
                .thenReturn(expectedTransferDTOs.get(0), expectedTransferDTOs.get(1));

        List<TransferDTO> result = transferHistoryService.getTransactionHistory(1L, 0, 5, "DESC");

        assertEquals(expectedTransferDTOs.size(), result.size());
        assertEquals(expectedTransferDTOs, result);

        verify(transferRepository).findBySourceAccountIdOrderByDateTimeDesc(1L, pageable);
        verify(transferMapper, times(transfers.size())).mapFrom(any(TransferEntity.class));
    }

    @Test
    void testGetTransactionHistoryWithNoTransfers() {
        var pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dateTime"));
        Page<TransferEntity> transfersPage = Page.empty();

        when(transferRepository.findBySourceAccountIdOrderByDateTimeDesc(1L, pageable)).thenReturn(transfersPage);

        List<TransferDTO> result = transferHistoryService.getTransactionHistory(1L, 0, 5, "DESC");

        assertTrue(result.isEmpty());

        verify(transferRepository).findBySourceAccountIdOrderByDateTimeDesc(1L, pageable);
        verifyNoInteractions(transferMapper);
    }

    @Test
    void testGetTransactionHistoryWithInvalidSortDirection() {
        var pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dateTime"));

        var exception = assertThrows(NullPointerException.class, () -> {
            transferHistoryService.getTransactionHistory(1L, 0, 5, "INVALID");
        });

        assertNotNull(exception);
        assertThat(exception.getMessage()).contains("Cannot invoke").contains("is null");

        verify(transferRepository).findBySourceAccountIdOrderByDateTimeDesc(1L, pageable);
        verifyNoInteractions(transferMapper);
    }

    private Page<TransferEntity> createMockTransfersPage() {
        List<TransferEntity> transfers = List.of(createMockTransferEntity(), createMockTransferEntity());
        return new PageImpl<>(transfers);
    }

    private TransferEntity createMockTransferEntity() {
        return new TransferEntity();
    }

    private List<TransferDTO> createMockTransferDTOs() {
        return List.of(createMockTransferDTO(), createMockTransferDTO());
    }

    private TransferDTO createMockTransferDTO() {
        return TransferDTO.builder().build();
    }
}
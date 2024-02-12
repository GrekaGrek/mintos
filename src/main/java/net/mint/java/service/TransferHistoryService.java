package net.mint.java.service;

import lombok.RequiredArgsConstructor;
import net.mint.java.domain.TransferEntity;
import net.mint.java.mapper.TransferMapper;
import net.mint.java.model.TransferDTO;
import net.mint.java.repository.TransferRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferHistoryService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;

    public List<TransferDTO> getTransactionHistory(Long accountId, int offset, int limit, String sortDirection) {
        Sort.Direction direction = Sort.Direction.DESC;
        if ("ASC".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(offset, limit, Sort.by(direction, "dateTime"));
        Page<TransferEntity> transfersPage = transferRepository.findBySourceAccountIdOrderByDateTimeDesc(accountId, pageable);
        List<TransferEntity> transfers = transfersPage.getContent();
        return transfers.stream()
                .map(transferMapper::mapFrom)
                .toList();
    }
}

package net.mint.java.resource;

import lombok.RequiredArgsConstructor;
import net.mint.java.model.TransferDTO;
import net.mint.java.service.TransferHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/transfer-history")
@RequiredArgsConstructor
public class TransferHistoryController {

    private final TransferHistoryService transferHistoryService;

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<List<TransferDTO>> getTransactionHistory(@PathVariable Long accountId,
                                                                   @RequestParam(defaultValue = "0") int offset,
                                                                   @RequestParam(defaultValue = "10") int limit,
                                                                   @RequestParam(defaultValue = "DESC") String sortDirection) {
        return new ResponseEntity<>(transferHistoryService.getTransactionHistory(accountId, offset, limit, sortDirection),
                HttpStatus.OK);
    }
}

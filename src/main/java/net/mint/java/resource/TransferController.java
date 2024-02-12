package net.mint.java.resource;

import lombok.RequiredArgsConstructor;
import net.mint.java.model.TransferDTO;
import net.mint.java.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/{fromAccountId}/{toAccountId}")
    public ResponseEntity<TransferDTO> transferFunds(@PathVariable Long fromAccountId,
                                                     @PathVariable Long toAccountId,
                                                     @RequestParam BigDecimal amount,
                                                     @RequestParam String currency) {
        TransferDTO transferDTO = transferService.transferFunds(fromAccountId, toAccountId, amount, currency);
        return new ResponseEntity<>(transferDTO, HttpStatus.CREATED);
    }
}

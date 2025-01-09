package com.example.resultbot.web.rest;

import com.example.resultbot.entity.Transaction;
import com.example.resultbot.service.TransactionService;
import com.example.resultbot.service.dto.TransactionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionResource {
    private final TransactionService transactionService;

    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TransactionDto transactionDto) throws URISyntaxException {
        TransactionDto result = transactionService.create(transactionDto);
        return ResponseEntity.created(new URI("/api/transaction/create/" + result.getId())).body(result);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody TransactionDto transactionDto, @PathVariable Long id) throws URISyntaxException {
        if (transactionDto.getId() != 0 && !transactionDto.getId().equals(id)) {
            return ResponseEntity.badRequest().body("Invalid id");
        }
        TransactionDto result = transactionService.update(transactionDto);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<TransactionDto> transactions = transactionService.findAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Transaction transaction = transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Transaction transaction = transactionService.delete(id);
        return ResponseEntity.ok(transaction);
    }
}


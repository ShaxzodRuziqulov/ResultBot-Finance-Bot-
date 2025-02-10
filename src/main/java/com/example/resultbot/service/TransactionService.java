package com.example.resultbot.service;

import com.example.resultbot.entity.Transaction;
import com.example.resultbot.entity.enumirated.Status;
import com.example.resultbot.repository.TransactionRepository;
import com.example.resultbot.service.dto.TransactionDto;
import com.example.resultbot.service.mapper.TransactionMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public TransactionDto create(TransactionDto transactionDto) {
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(transaction);
    }

    public TransactionDto update(TransactionDto transactionDto) {
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(transaction);
    }

    public List<TransactionDto> findAllTransactions() {
        return transactionRepository
                .findAll()
                .stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    public Transaction findById(Long id) {
        return transactionRepository
                .findById(id)
                .orElseGet(Transaction::new);
    }

    public Transaction delete(Long id) {
        return transactionRepository.updateStatus(id, Status.DELETE);
    }

    public List<Transaction> fetchMonthlyTransactions(int currentMonth, int currentYear) {
        return transactionRepository.findMonthlyTransactions(currentMonth, currentYear);
    }
}

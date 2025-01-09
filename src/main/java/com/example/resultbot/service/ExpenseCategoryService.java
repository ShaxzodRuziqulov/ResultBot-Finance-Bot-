package com.example.resultbot.service;

import com.example.resultbot.entity.ExpenseCategory;
import com.example.resultbot.entity.enumirated.Status;
import com.example.resultbot.repository.ExpenseCategoryRepository;
import com.example.resultbot.service.dto.ExpenseCategoryDto;
import com.example.resultbot.service.mapper.ExpenseCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseCategoryMapper expenseCategoryMapper;

    public ExpenseCategoryService(ExpenseCategoryRepository expenseCategoryRepository, ExpenseCategoryMapper expenseCategoryMapper) {
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.expenseCategoryMapper = expenseCategoryMapper;
    }

    public ExpenseCategoryDto create(ExpenseCategoryDto expenseCategoryDto) {
        ExpenseCategory expenseCategory = expenseCategoryMapper.toEntity(expenseCategoryDto);
        expenseCategory = expenseCategoryRepository.save(expenseCategory);
        return expenseCategoryMapper.toDto(expenseCategory);
    }

    public ExpenseCategoryDto update(ExpenseCategoryDto expenseCategoryDto) {
        ExpenseCategory expenseCategory = expenseCategoryMapper.toEntity(expenseCategoryDto);
        expenseCategory = expenseCategoryRepository.save(expenseCategory);
        return expenseCategoryMapper.toDto(expenseCategory);
    }

    public List<ExpenseCategoryDto> findAllExpenseCategories() {
        return expenseCategoryRepository
                .findAll()
                .stream()
                .map(expenseCategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public ExpenseCategory findById(Long id) {
        return expenseCategoryRepository
                .findById(id)
                .orElseGet(ExpenseCategory::new);
    }

    public ExpenseCategory delete(Long id) {
        return expenseCategoryRepository.updateStatus(id, Status.DELETE);
    }
}


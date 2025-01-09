package com.example.resultbot.service.mapper;

import com.example.resultbot.entity.ExpenseCategory;
import com.example.resultbot.entity.Transaction;
import com.example.resultbot.service.dto.ExpenseCategoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExpenseCategoryMapper extends EntityMapper<ExpenseCategoryDto, ExpenseCategory> {
    ExpenseCategory toEntity(ExpenseCategoryDto expenseCategoryDto);

    ExpenseCategoryDto toDto(ExpenseCategory expenseCategory);
}

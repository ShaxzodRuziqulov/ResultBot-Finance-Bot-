package com.example.resultbot.service.mapper;

import com.example.resultbot.entity.Client;
import com.example.resultbot.entity.ExpenseCategory;
import com.example.resultbot.entity.ServiceType;
import com.example.resultbot.entity.Transaction;
import com.example.resultbot.service.dto.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDto, Transaction> {
    @Mapping(source = "clientId", target = "client.id")
    @Mapping(source = "serviceTypeId", target = "serviceType.id")
    @Mapping(source = "expenseCategoryId", target = "expenseCategory.id")
    Transaction toEntity(TransactionDto transactionDto);

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "serviceType.id", target = "serviceTypeId")
    @Mapping(source = "expenseCategory.id", target = "expenseCategoryId")
    TransactionDto toDto(Transaction transaction);



}

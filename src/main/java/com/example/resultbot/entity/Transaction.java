package com.example.resultbot.entity;

import com.example.resultbot.entity.enumirated.CurrencyType;
import com.example.resultbot.entity.enumirated.Status;
import com.example.resultbot.entity.enumirated.TransactionStatus;
import com.example.resultbot.entity.enumirated.TransactionType;
import com.example.resultbot.entity.teamplated.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity(name = "transaction")
public class Transaction extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Transaction type cannot be null")
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Currency type cannot be null")
    private CurrencyType currency;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    private ExpenseCategory expenseCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading qo‘shildi
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Transaction status cannot be null")
    private TransactionStatus transactionStatus;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status cannot be null")
    private Status status;

    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;

    private String filePath; // Fayl yo‘li uchun maydon

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

package com.example.resultbot.repository;

import com.example.resultbot.entity.Transaction;
import com.example.resultbot.entity.enumirated.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    @Modifying
    @Query("update transaction t set t.status=:status where t.id=:id")
    Transaction updateStatus(@Param("id") Long id, @Param("status") Status status);
    @Query("SELECT t FROM transaction t WHERE MONTH(t.createdAt) = :month AND YEAR(t.createdAt) = :year")
    List<Transaction> findMonthlyTransactions(@Param("month") int month, @Param("year") int year);

}

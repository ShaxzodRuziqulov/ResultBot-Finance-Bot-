package com.example.resultbot.repository;

import com.example.resultbot.entity.Client;
import com.example.resultbot.entity.Transaction;
import com.example.resultbot.entity.enumirated.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    @Query("update transaction t set t.status=:status where t.id=:id")
    Transaction updateStatus(@Param("id") Long id, @Param("status") Status status);
}

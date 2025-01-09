package com.example.resultbot.repository;

import com.example.resultbot.entity.Client;
import com.example.resultbot.entity.enumirated.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("update client c set c=:status where c.id=:id")
    Client updateStatus(@Param("id") Long id, @Param("status") Status status);
}

package com.example.resultbot.repository;

import com.example.resultbot.entity.Client;
import com.example.resultbot.entity.ServiceType;
import com.example.resultbot.entity.enumirated.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType,Long> {
    @Query("update service_type s set s.status=:status where s.id=:id")
    ServiceType updateStatus(@Param("id") Long id, @Param("status") Status status);
}

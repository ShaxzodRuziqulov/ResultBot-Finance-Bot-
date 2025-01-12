package com.example.resultbot.repository;

import com.example.resultbot.entity.User;
import com.example.resultbot.entity.enumirated.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("update User u set u.status=:status where u.id=:id")
    User updateStatus(@Param("id") Long id, @Param("status") Status status);

    Optional<User> findByEmail(String email);

    User findByVerificationCode(@Param("code") String code);

    List<User> findAllByStatus(Status status);


    @Query("SELECT u FROM User u WHERE u.chatId = :chatId")
    Optional<User> findByChatId(@Param("chatId") Long chatId);


    boolean existsByEmail(String email);
}

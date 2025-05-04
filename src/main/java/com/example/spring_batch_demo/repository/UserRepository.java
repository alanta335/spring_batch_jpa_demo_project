package com.example.spring_batch_demo.repository;

import com.example.spring_batch_demo.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.address = :#{#user.address} WHERE u.id = :#{#user.id}")
    int updateUserAddress(@Param("user") User user);
}

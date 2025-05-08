package com.example.spring_batch_demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users", schema = "mydb")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    private int id;
    private String name;
    private String email;
    private String address;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime created;
    @LastModifiedDate
    private LocalDateTime lastUpdated;
}

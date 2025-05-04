package com.example.spring_batch_demo.batch.listener;

import com.example.spring_batch_demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserItemReadListener implements ItemReadListener<User> {
    @Override
    public void beforeRead() {
        log.info("Starting to read a person record...");
    }

    @Override
    public void afterRead(User user) {
        log.info("Successfully read: {}", user);
    }

    @Override
    public void onReadError(Exception ex) {
        log.error("Error reading person record: {}", ex.getMessage(), ex);
    }
}

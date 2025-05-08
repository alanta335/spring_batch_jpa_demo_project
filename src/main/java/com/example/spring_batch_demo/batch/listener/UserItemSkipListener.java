package com.example.spring_batch_demo.batch.listener;

import com.example.spring_batch_demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserItemSkipListener implements SkipListener<User, User> {
    @Override
    public void onSkipInRead(Throwable t) {
        log.error("Error reading person record: {}", t.getMessage(), t);
    }

    @Override
    public void onSkipInWrite(User item, Throwable t) {
        log.error("Error writing person record: {}", t.getMessage(), t);
    }

    @Override
    public void onSkipInProcess(User item, Throwable t) {
        log.error("Error processing person record: {}", t.getMessage(), t);
    }
}

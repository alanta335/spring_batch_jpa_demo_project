package com.example.spring_batch_demo.batch.processer;

import com.example.spring_batch_demo.entity.User;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class UserItemProcessor implements ItemProcessor<User, User> {
    @Override
    public User process(User item) {
        if (item.getId() == 5 || item.getId() == 15) {
            throw new RuntimeException("Error processing user record");
        }
        item.setName(item.getName().toUpperCase());
        return item;
    }
}

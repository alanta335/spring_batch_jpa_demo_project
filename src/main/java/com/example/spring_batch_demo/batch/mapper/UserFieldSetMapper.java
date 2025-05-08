package com.example.spring_batch_demo.batch.mapper;

import com.example.spring_batch_demo.entity.User;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class UserFieldSetMapper implements FieldSetMapper<User> {

    @Override
    public User mapFieldSet(FieldSet fieldSet) {
        if (fieldSet.readInt("id") == 1) {
            throw new RuntimeException("Error reading user record");
        }
        return User.builder()
                .id(fieldSet.readInt("id"))
                .name(fieldSet.readString("name"))
                .email(fieldSet.readString("email"))
                .build();
    }
}

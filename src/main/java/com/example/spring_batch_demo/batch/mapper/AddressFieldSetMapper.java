package com.example.spring_batch_demo.batch.mapper;

import com.example.spring_batch_demo.entity.User;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class AddressFieldSetMapper implements FieldSetMapper<User> {

    @Override
    public User mapFieldSet(FieldSet fieldSet) {
        return User.builder()
                .id(fieldSet.readInt("id"))
                .address(fieldSet.readString("address"))
                .build();
    }
}

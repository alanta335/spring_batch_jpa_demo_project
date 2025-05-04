package com.example.spring_batch_demo.batch.reader;

import com.example.spring_batch_demo.batch.mapper.AddressFieldSetMapper;
import com.example.spring_batch_demo.batch.mapper.UserFieldSetMapper;
import com.example.spring_batch_demo.entity.User;
import com.example.spring_batch_demo.repository.UserRepository;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;

import java.util.Collections;

@Configuration
public class CsvReader {
    @Bean("userCsvReader")
    public FlatFileItemReader<User> userCsvReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userFileItemReader")
                .resource(new ClassPathResource("csv/user.csv"))
                .delimited()
                .names("id", "name", "email")
                .fieldSetMapper(new UserFieldSetMapper())
                .linesToSkip(1) // Skip the header line
                .build();
    }

    @Bean("addressCsvReader")
    public FlatFileItemReader<User> addressCsvReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userFileItemReader")
                .resource(new ClassPathResource("csv/address.csv"))
                .delimited()
                .names("id", "address")
                .fieldSetMapper(new AddressFieldSetMapper())
                .linesToSkip(1) // Skip the header line
                .build();
    }

    @Bean
    public RepositoryItemReader<User> userRepositoryReader(UserRepository userRepository) {
        return new RepositoryItemReaderBuilder<User>()
                .name("dbUserReader")
                .repository(userRepository)
                .methodName("findAll")
                .arguments(Collections.emptyList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }
}

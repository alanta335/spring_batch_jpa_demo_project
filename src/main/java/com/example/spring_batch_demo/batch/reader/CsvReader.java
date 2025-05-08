package com.example.spring_batch_demo.batch.reader;

import com.example.spring_batch_demo.batch.mapper.AddressFieldSetMapper;
import com.example.spring_batch_demo.batch.mapper.UserFieldSetMapper;
import com.example.spring_batch_demo.entity.User;
import com.example.spring_batch_demo.repository.UserRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;

import java.util.Collections;

@Configuration
public class CsvReader {
    @Bean("userCsvReader")
    @StepScope
    public FlatFileItemReader<User> userCsvReader(@Value("#{jobParameters['filePath']}") String userCsvPath) {
        return new FlatFileItemReaderBuilder<User>()
                .name("userFileItemReader")
                .resource(new FileSystemResource(userCsvPath))
                .delimited()
                .names("id", "name", "email")
                .fieldSetMapper(new UserFieldSetMapper())
                .linesToSkip(1) // Skip the header line
                .build();
    }

    @Bean("addressCsvReader")
    @StepScope
    public FlatFileItemReader<User> addressCsvReader(@Value("#{jobParameters['filePath']}") String addressCsvPath) {
        return new FlatFileItemReaderBuilder<User>()
                .name("userFileItemReader")
                .resource(new FileSystemResource(addressCsvPath))
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

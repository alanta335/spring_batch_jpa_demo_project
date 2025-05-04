package com.example.spring_batch_demo.batch.writer;

import com.example.spring_batch_demo.entity.User;
import com.example.spring_batch_demo.repository.UserRepository;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class UserWriter {

    @Bean("userItemWriter")
    public RepositoryItemWriter<User> userItemWriter(UserRepository repository) {
        return new RepositoryItemWriterBuilder<User>().repository(repository).methodName("save").build();
    }

    @Bean("addressItemWriter")
    public RepositoryItemWriter<User> addressItemWriter(UserRepository repository) {
        return new RepositoryItemWriterBuilder<User>().repository(repository).methodName("updateUserAddress").build();
    }

    @Bean
    public FlatFileItemWriter<User> flatFileUserWriter() {
        return new FlatFileItemWriterBuilder<User>()
                .name("userItemWriter")
                .resource(new FileSystemResource("src/main/resources/output/users.csv"))
                .delimited()
                .delimiter(",")
                .names("id", "name", "email", "address")
                .build();
    }
}

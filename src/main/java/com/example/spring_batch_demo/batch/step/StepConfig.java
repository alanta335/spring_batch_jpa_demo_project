package com.example.spring_batch_demo.batch.step;

import com.example.spring_batch_demo.batch.listener.FileKeyStepListener;
import com.example.spring_batch_demo.batch.listener.UserItemReadListener;
import com.example.spring_batch_demo.batch.listener.UserItemSkipListener;
import com.example.spring_batch_demo.batch.processer.UserItemProcessor;
import com.example.spring_batch_demo.entity.User;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class StepConfig {
    private final JobRepository jobRepository;
    private final FlatFileItemReader<User> userFlatFileItemReader;
    private final FlatFileItemReader<User> addressFlatFileItemReader;
    private final RepositoryItemWriter<User> userRepositoryItemWriter;
    private final RepositoryItemWriter<User> addressRepositoryItemWriter;
    private final UserItemReadListener userItemReadListener;
    private final RepositoryItemReader<User> repositoryItemReader;
    private final FlatFileItemWriter<User> flatFileItemWriter;

    public StepConfig(JobRepository jobRepository,
                      @Qualifier("userCsvReader")
                      FlatFileItemReader<User> userFlatFileItemReader,
                      @Qualifier("addressCsvReader")
                      FlatFileItemReader<User> addressFlatFileItemReader,
                      @Qualifier("userItemWriter")
                      RepositoryItemWriter<User> userRepositoryItemWriter,
                      @Qualifier("addressItemWriter")
                      RepositoryItemWriter<User> addressRepositoryItemWriter,
                      UserItemReadListener userItemReadListener,
                      RepositoryItemReader<User> repositoryItemReader,
                      FlatFileItemWriter<User> flatFileItemWriter
    ) {
        this.jobRepository = jobRepository;
        this.userFlatFileItemReader = userFlatFileItemReader;
        this.userRepositoryItemWriter = userRepositoryItemWriter;
        this.userItemReadListener = userItemReadListener;
        this.addressFlatFileItemReader = addressFlatFileItemReader;
        this.addressRepositoryItemWriter = addressRepositoryItemWriter;
        this.repositoryItemReader = repositoryItemReader;
        this.flatFileItemWriter = flatFileItemWriter;
    }

    @Bean("user-csv-to-db")
    public Step userCsvToDbStep(PlatformTransactionManager transactionManager,
                                UserItemSkipListener userItemSkipListener,
                                FileKeyStepListener fileKeyStepListener,
                                UserItemProcessor userItemProcessor) {
        return new StepBuilder("user-csv-to-db", jobRepository)
                .<User, User>chunk(2, transactionManager)  // commit every 100 items
                .reader(userFlatFileItemReader)
                .writer(userRepositoryItemWriter)
                .processor(userItemProcessor)
                .listener(userItemReadListener)
                .faultTolerant()                // enable skip/retry
                .listener(userItemSkipListener)
                .skipLimit(10)                 // skip up to 10 bad items
                .skip(Exception.class)
                .listener(fileKeyStepListener)// skip on all exceptions (customize as needed)
                .build();
    }

    @Bean("address-csv-to-db")
    public Step addressCsvToDbStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("address-csv-to-db", jobRepository)
                .<User, User>chunk(2, transactionManager)  // commit every 100 items
                .reader(addressFlatFileItemReader)
                .writer(addressRepositoryItemWriter)
                .listener(userItemReadListener)
                .faultTolerant()                // enable skip/retry
                .skipLimit(10)                 // skip up to 10 bad items
                .skip(Exception.class)         // skip on all exceptions (customize as needed)
                .build();
    }

    @Bean("user-db-to-csv")
    public Step userDbToCsvStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("user-db-to-csv", jobRepository)
                .<User, User>chunk(2, transactionManager)  // commit every 100 items
                .reader(repositoryItemReader)
                .writer(flatFileItemWriter)
                .listener(userItemReadListener)
                .faultTolerant()// enable skip/retry
                .skipLimit(10)                 // skip up to 10 bad items
                .skip(Exception.class)         // skip on all exceptions (customize as needed)
                .build();
    }
}

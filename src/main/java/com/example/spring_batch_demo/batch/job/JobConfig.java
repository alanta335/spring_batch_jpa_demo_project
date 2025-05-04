package com.example.spring_batch_demo.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig {

    private final JobRepository jobRepository;
    private final Step userCsvToDbStep;
    private final Step addressCsvToDbStep;
    private final Step userDbToCsvStep;

    public JobConfig(JobRepository jobRepository,
                     @Qualifier("user-csv-to-db") Step userCsvToDbStep,
                     @Qualifier("address-csv-to-db") Step addressCsvToDbStep,
                     @Qualifier("user-db-to-csv") Step userDbToCsvStep
    ) {
        this.jobRepository = jobRepository;
        this.userCsvToDbStep = userCsvToDbStep;
        this.addressCsvToDbStep = addressCsvToDbStep;
        this.userDbToCsvStep = userDbToCsvStep;
    }

    @Bean
    public Job processUserDataJob() {
        return new JobBuilder("csv-to-db-to-csv", jobRepository)
                .start(userCsvToDbStep)
                .next(addressCsvToDbStep)
                .next(userDbToCsvStep)
                .build();
    }
}

package com.example.spring_batch_demo.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job processUserDataJob;
    private final S3Client s3Client;

    //    @Scheduled(cron = "0 0 12 * * ?", zone = "UTC")
    @EventListener(ApplicationReadyEvent.class)
    public void runFootballJob() throws Exception {
        try {
            List<File> userFiles = getFilesFromS3("user/");
            List<File> addressFiles = getFilesFromS3("address/");
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
//                    .addString("user", "csv/user.csv")
//                    .addString("address", "csv/address.csv")
                    .toJobParameters();
            jobLauncher.run(processUserDataJob, params);
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("Error running job: {}", e.getMessage());
        }
    }

    public List<File> getFilesFromS3(String path) {
        List<File> downloadedFiles = new ArrayList<>();

        String mybucket = "mybucket";

        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(mybucket)
                .prefix(path)
                .build();
        s3Client.listObjectsV2(listReq).contents().stream().map(S3Object::key).forEach(key -> {
            log.info("Downloading: {}", key);

            try {
                // Create a temp file with a unique suffix to avoid conflicts
                Path tempPath = Files.createTempFile("s3_", "_" + UUID.randomUUID().toString() + ".tmp");

                // Download file from S3
                GetObjectRequest getReq = GetObjectRequest.builder()
                        .bucket(mybucket)
                        .key(key)
                        .build();

                s3Client.getObject(getReq, ResponseTransformer.toFile(tempPath));

                // Add the downloaded file to the list
                downloadedFiles.add(tempPath.toFile());
            } catch (IOException e) {
                log.error("Failed to download file: " + key, e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return downloadedFiles;
    }
}

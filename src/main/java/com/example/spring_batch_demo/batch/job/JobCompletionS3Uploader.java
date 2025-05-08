package com.example.spring_batch_demo.batch.job;

import com.example.spring_batch_demo.batch.listener.UserItemSkipListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCompletionS3Uploader implements JobExecutionListener {

    private final UserItemSkipListener skipListener; // Injected
    private final S3Client s3Client;

    private final String bucketName = "mybucket";
    private final String s3Key = "batch-errors/error-log.csv";

    @Override
    public void afterJob(JobExecution jobExecution) {
        String errorFilePath = skipListener.getErrorFilePath();
        if (errorFilePath != null) {
            try {
                Path path = Path.of(errorFilePath);
                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build();

                s3Client.putObject(request, RequestBody.fromFile(path));
                log.info("Uploaded error log to S3: s3://{}/{}", bucketName, s3Key);
            } catch (Exception e) {
                log.error("Failed to upload error file to S3", e);
            }
        }
    }
}

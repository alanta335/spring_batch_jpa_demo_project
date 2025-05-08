package com.example.spring_batch_demo.batch.job;

import com.example.spring_batch_demo.batch.writer.ErrorFileWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCompletionS3Uploader implements JobExecutionListener {

    private final ErrorFileWriter errorFileWriter;
    private final S3Client s3Client;

    private final String bucketName = "mybucket";

    @Override
    public void afterJob(JobExecution jobExecution) {
        errorFileWriter.getErrorFiles().forEach((fileKey, path) -> {
            try {
                String s3Key = "errors/" + path.getFileName();
                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build();

                s3Client.putObject(request, RequestBody.fromFile(path));
                log.info("Uploaded error file for {} to S3 as {}", fileKey, s3Key);
            } catch (Exception e) {
                log.error("Failed to upload error file for {} to S3", fileKey, e);
            }
        });

        errorFileWriter.closeAll();
    }
}

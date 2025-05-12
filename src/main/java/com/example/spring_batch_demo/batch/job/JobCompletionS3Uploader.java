package com.example.spring_batch_demo.batch.job;

import com.example.spring_batch_demo.batch.writer.ErrorFileWriter;
import com.example.spring_batch_demo.enumaration.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCompletionS3Uploader implements JobExecutionListener {

    public static final String ERRORS_PATH = "errors/";
    public static final String BACKSLASH = "/";
    private final ErrorFileWriter errorFileWriter;
    private final S3Client s3Client;

    private final String bucketName = "mybucket";

    @Override
    public void afterJob(JobExecution jobExecution) {
        try {
            errorFileWriter.getErrorFiles().forEach(this::uploadErrorFileToS3);
        } catch (Exception e) {
            log.error("Error during job completion: ", e);
        } finally {
            errorFileWriter.closeAll();
        }
    }

    private void uploadErrorFileToS3(String fileKey, Path path) {
        try {
            String s3Key = generateS3Key(path);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.putObject(request, RequestBody.fromFile(path));
            log.info("Uploaded error file for {} to S3 as {}", fileKey, s3Key);
        } catch (Exception e) {
            log.error("Failed to upload error file for {} to S3", fileKey, e);
        }
    }

    private String generateS3Key(Path path) {
        ErrorType errorType = getMatchingErrorType(path);
        String fileName = path.getFileName().toString();
        return ERRORS_PATH + errorType.name().toLowerCase() + BACKSLASH + fileName;
    }

    private ErrorType getMatchingErrorType(Path path) {
        String fileName = path.getFileName().toString();
        return Arrays.stream(ErrorType.values())
                .filter(errorType -> fileName.contains(errorType.name()))
                .findFirst()
                .orElse(null);
    }
}

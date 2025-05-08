package com.example.spring_batch_demo.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class BatchScheduler {
    public static final String TMP_FILE = "/tmp/file/";
    public static final String BUCKET = "mybucket";

    private final JobLauncher jobLauncher;
    private final Job addressDataProcessingJob;
    private final Job dataExportingJob;
    private final Job userDataProcessingJob;
    private final S3Client s3Client;

    public BatchScheduler(@Qualifier("processUserData") Job userDataProcessingJob,
                          @Qualifier("processAddressData") Job addressDataProcessingJob,
                          @Qualifier("exportData") Job dataExportingJob,
                          JobLauncher jobLauncher, S3Client s3Client) {
        this.userDataProcessingJob = userDataProcessingJob;
        this.addressDataProcessingJob = addressDataProcessingJob;
        this.dataExportingJob = dataExportingJob;
        this.jobLauncher = jobLauncher;
        this.s3Client = s3Client;
    }

    //    @Scheduled(cron = "0 0 12 * * ?", zone = "UTC")
    @EventListener(ApplicationReadyEvent.class)
    public void runJobs() {
        processFiles("user/", userDataProcessingJob);
        processFiles("address/", addressDataProcessingJob);
        runJob(dataExportingJob, new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters());
    }

    private void processFiles(String path, Job job) {
        List<File> files = getFilesFromS3(path);
        if (Objects.nonNull(files)) {
            files.forEach(file -> {
                JobParameters params = buildJobParameter(file);
                runJob(job, params);
                deleteFile(file);
            });
        }
    }

    private void runJob(Job job, JobParameters params) {
        try {
            jobLauncher.run(job, params);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error("Error running job: {}", e.getMessage(), e);
        }
    }

    private static JobParameters buildJobParameter(File file) {
        return new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("filePath", file.getAbsolutePath())
                .toJobParameters();
    }

    private static void deleteFile(File file) {
        if (file.delete()) {
            log.info("Deleted file: {} ", file.getAbsolutePath());
        } else {
            log.warn("Failed to delete file: {}", file.getAbsolutePath());
        }
    }

    public List<File> getFilesFromS3(String path) {
        List<File> downloadedFiles = new ArrayList<>();

        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(BUCKET)
                .prefix(path)
                .build();
        s3Client.listObjectsV2(listReq).contents().stream().map(S3Object::key).forEach(key -> {
            log.info("Downloading: {}", key);
            downloadFileFromS3(key);
            File file = new File(TMP_FILE + key);
            downloadedFiles.add(file);
        });
        return downloadedFiles;
    }

    private void downloadFileFromS3(final String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BatchScheduler.BUCKET)
                .key(objectKey)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3ObjectInputStream = s3Client.getObject(getObjectRequest)) {
            File targetFile = new File(BatchScheduler.TMP_FILE + objectKey);
            FileUtils.copyInputStreamToFile(s3ObjectInputStream, targetFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

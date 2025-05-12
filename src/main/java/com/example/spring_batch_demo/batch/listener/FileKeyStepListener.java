package com.example.spring_batch_demo.batch.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileKeyStepListener implements StepExecutionListener {

    private final UserItemSkipListener skipListener;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        try {
            String inputFile = stepExecution.getJobParameters().getString("filePath");
            if (inputFile != null) {
                File file = new File(inputFile);
                String fileName = file.getName();
                String fileKey = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                skipListener.setCurrentFileKey(fileKey);
            }
        } catch (Exception e) {
            log.error("Error setting file key in beforeStep: ", e);
            throw new RuntimeException("Error setting file key", e);
        }
    }
}


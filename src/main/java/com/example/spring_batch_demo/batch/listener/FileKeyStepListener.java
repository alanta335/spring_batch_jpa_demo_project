package com.example.spring_batch_demo.batch.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class FileKeyStepListener implements StepExecutionListener {

    private final UserItemSkipListener skipListener;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String inputFile = stepExecution.getJobParameters().getString("filePath");
        if (inputFile != null) {
            File file = new File(inputFile);
            String fileKey = file.getName(); // Or strip extension, hash, etc.
            skipListener.setCurrentFileKey(fileKey);
        }
    }
}


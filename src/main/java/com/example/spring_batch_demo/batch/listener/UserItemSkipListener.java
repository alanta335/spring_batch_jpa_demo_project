package com.example.spring_batch_demo.batch.listener;

import com.example.spring_batch_demo.batch.writer.ErrorFileWriter;
import com.example.spring_batch_demo.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserItemSkipListener implements SkipListener<User, User> {

    private ErrorFileWriter errorFileWriter;

    @PostConstruct
    public void init() {
        try {
            errorFileWriter = new ErrorFileWriter();
        } catch (Exception e) {
            log.error("Failed to initialize ErrorFileWriter", e);
        }
    }

    @Override
    public void onSkipInRead(Throwable t) {
        log.error("Error reading record: {}", t.getMessage(), t);
        errorFileWriter.writeError("READ", null, t);
    }

    @Override
    public void onSkipInWrite(User item, Throwable t) {
        log.error("Error writing record: {}", t.getMessage(), t);
        errorFileWriter.writeError("WRITE", item, t);
    }

    @Override
    public void onSkipInProcess(User item, Throwable t) {
        log.error("Error processing record: {}", t.getMessage(), t);
        errorFileWriter.writeError("PROCESS", item, t);
    }

    @PreDestroy
    public void cleanup() {
        errorFileWriter.close();
    }

    public String getErrorFilePath() {
        return errorFileWriter.getTempFilePath().toString();
    }
}

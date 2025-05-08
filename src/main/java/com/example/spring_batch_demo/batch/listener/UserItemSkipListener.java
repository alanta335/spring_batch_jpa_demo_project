package com.example.spring_batch_demo.batch.listener;

import com.example.spring_batch_demo.batch.writer.ErrorFileWriter;
import com.example.spring_batch_demo.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserItemSkipListener implements SkipListener<User, User> {

    private final ErrorFileWriter errorFileWriter;

    @Setter
    private String currentFileKey;

    @Override
    public void onSkipInRead(Throwable t) {
        errorFileWriter.writeError(currentFileKey, "READ", null, t);
    }

    @Override
    public void onSkipInWrite(User item, Throwable t) {
        errorFileWriter.writeError(currentFileKey, "WRITE", item, t);
    }

    @Override
    public void onSkipInProcess(User item, Throwable t) {
        errorFileWriter.writeError(currentFileKey, "PROCESS", item, t);
    }
}

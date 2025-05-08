package com.example.spring_batch_demo.batch.writer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
public class ErrorFileWriter {

    @Getter
    private final Path tempFilePath;

    private final BufferedWriter writer;

    public ErrorFileWriter() throws IOException {
        this.tempFilePath = Files.createTempFile("error-log-", ".csv");
        this.writer = Files.newBufferedWriter(tempFilePath, StandardOpenOption.APPEND);
        log.info("Temporary error log created at: {}", tempFilePath);
    }

    public synchronized void writeError(String phase, Object item, Throwable t) {
        try {
            writer.write(String.format("[%s] Error: %s | Item: %s%n", phase, t.getMessage(), item));
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to write error to temp file", e);
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            log.error("Failed to close temp error file writer", e);
        }
    }
}


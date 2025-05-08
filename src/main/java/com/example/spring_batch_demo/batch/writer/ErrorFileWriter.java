package com.example.spring_batch_demo.batch.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ErrorFileWriter {

    private static final String ERROR_FILE_PREFIX = "error-";
    private static final String ERROR_FILE_SUFFIX = ".log";
    private static final StandardOpenOption WRITE_OPTION = StandardOpenOption.APPEND;

    private final ConcurrentHashMap<String, BufferedWriter> errorWriters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Path> errorPaths = new ConcurrentHashMap<>();

    public void writeError(String key, String phase, Object item, Throwable t) {
        try {
            try (BufferedWriter writer = errorWriters.computeIfAbsent(key, this::initializeWriter)) {
                writer.write(formatErrorEntry(phase, t, item));
                writer.flush();
            }
        } catch (IOException e) {
            log.error("Failed to write error for key={}", key, e);
        }
    }

    private BufferedWriter initializeWriter(String key) {
        try {
            Path tempFile = Files.createTempFile(ERROR_FILE_PREFIX + key, ERROR_FILE_SUFFIX);
            errorPaths.put(key, tempFile);
            return Files.newBufferedWriter(tempFile, WRITE_OPTION);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create error log for key: " + key, e);
        }
    }

    private String formatErrorEntry(String phase, Throwable t, Object item) {
        return String.format("[%s] Error: %s | Item: %s%n", phase, t.getMessage(), item);
    }


    public void closeAll() {
        errorWriters.forEach((k, w) -> {
            try {
                w.close();
            } catch (IOException e) {
                log.warn("Failed to close writer for {}", k, e);
            }
        });
    }

    public ConcurrentHashMap<String, Path> getErrorFiles() {
        return errorPaths;
    }
}


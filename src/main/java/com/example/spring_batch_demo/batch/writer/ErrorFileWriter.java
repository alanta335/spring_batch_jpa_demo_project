package com.example.spring_batch_demo.batch.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ErrorFileWriter {

    private final ConcurrentHashMap<String, BufferedWriter> writerMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Path> pathMap = new ConcurrentHashMap<>();

    public void writeError(String fileKey, String phase, Object item, Throwable t) {
        try {
            BufferedWriter writer = writerMap.computeIfAbsent(fileKey, key -> {
                try {
                    Path tempFile = Files.createTempFile("error-" + key, ".log");
                    pathMap.put(key, tempFile);
                    return Files.newBufferedWriter(tempFile, StandardOpenOption.APPEND);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create error log file for key: " + key, e);
                }
            });

            writer.write(String.format("[%s] Error: %s | Item: %s%n", phase, t.getMessage(), item));
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to write error for fileKey={}", fileKey, e);
        }
    }

    public void closeAll() {
        writerMap.forEach((key, writer) -> {
            try {
                writer.close();
            } catch (IOException e) {
                log.warn("Failed to close writer for {}", key, e);
            }
        });
    }

    public ConcurrentHashMap<String, Path> getErrorFiles() {
        return pathMap;
    }
}


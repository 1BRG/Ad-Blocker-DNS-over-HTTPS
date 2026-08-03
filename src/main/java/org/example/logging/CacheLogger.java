package org.example.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.LinkedBlockingQueue;

public class CacheLogger {
    private final LinkedBlockingQueue<String> logQueue;
    private final Path logsFilePath;

    public CacheLogger(Path logsFilePath, int capacity) {
        this.logsFilePath = logsFilePath;
        this.logQueue = new LinkedBlockingQueue<>(capacity);

        Thread writingThread = new Thread(this::processQueue, "Logger Thread");
        writingThread.setDaemon(true);
        writingThread.start();
    }

    public void log(String message) {
        logQueue.offer(message);
    }

    private void processQueue() {
        try (BufferedWriter writer = Files.newBufferedWriter(logsFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            while (true) {
                String log = logQueue.take();
                writer.write(log);
                writer.newLine();
                writer.flush();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

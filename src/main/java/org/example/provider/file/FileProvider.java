package org.example.provider.file;

import org.example.provider.BlocklistProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileProvider implements BlocklistProvider {

    private final Path filePath;

    public FileProvider(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Set<String> loadBlockedDomains() {
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("File path doesn't exists:" + filePath.toAbsolutePath());
        }

        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.map(String::trim).filter(line -> !line.isEmpty()).collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read blocklist file", e);
        }
    }
}

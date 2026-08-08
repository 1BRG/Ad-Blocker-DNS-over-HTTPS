package org.example.provider;

import org.example.provider.file.FileProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FileProviderTest {

    @TempDir
    Path tempDir;
    @Test
    void loadBlockedDomain_PathDoesNotExist_ShouldThrowIllegalArgumentException()
    {

        Path invaliFilePath = tempDir.resolve("file_path_does_not_exist.txt");

        FileProvider fileProvider = new FileProvider(invaliFilePath);

        assertThrows(IllegalArgumentException.class, fileProvider::loadBlockedDomains);
    }

    @Test
    void loadBlockedDomain_FileContainsEmptyLines_ReturnListWithoutEmptyLines() throws IOException {
        Path emptyLinesFilePath = tempDir.resolve("empty_lines.txt");
        List<String> contentWithEmptyLines = List.of("abc.com", "", "", "xyz.com");
        Files.write(emptyLinesFilePath, contentWithEmptyLines);

        FileProvider fileProvider = new FileProvider(emptyLinesFilePath);
        Set<String> result = fileProvider.loadBlockedDomains();

        assertEquals(2, result.size());
        assertTrue(result.contains("abc.com"));
        assertTrue(result.contains("xyz.com"));

    }

    @Test
    void loadBlockedDomain_FileContainsOnlyNonEmptyDomains_ReturnTheSetList() throws IOException {
        Path domainsOnlyFilePath = tempDir.resolve("only_domains.txt");
        List<String> contentWithEmptyLines = List.of("abc.com", "xyz.com");
        Files.write(domainsOnlyFilePath, contentWithEmptyLines);

        FileProvider fileProvider = new FileProvider(domainsOnlyFilePath);
        Set<String> result = fileProvider.loadBlockedDomains();

        assertEquals(2, result.size());
        assertTrue(result.contains("abc.com"));
        assertTrue(result.contains("xyz.com"));

    }
}

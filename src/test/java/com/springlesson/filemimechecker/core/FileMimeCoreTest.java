package com.springlesson.filemimechecker.core;

import com.springlesson.filemimechecker.core.model.MimeDetectionResult;
import com.springlesson.filemimechecker.core.strategy.MimeDetectionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FileMimeCoreTest {

    private FileMimeCore fileMimeCore;
    private MimeDetectionStrategy mockStrategy;

    @BeforeEach
    public void setUp() {
        mockStrategy = Mockito.mock(MimeDetectionStrategy.class);
        fileMimeCore = new FileMimeCore(mockStrategy);
    }

    @Test
    public void testProcessSingleFile() throws IOException {
        byte[] data = "test data".getBytes();
        when(mockStrategy.detect(data)).thenReturn("text/plain");

        MimeDetectionResult result = fileMimeCore.process(data);

        assertEquals("text/plain", result.getFormattedResult());
        assertEquals(1, result.getMimeTypes().size());
    }

    @Test
    public void testProcessZipFile() throws IOException {
        // Create a mock zip content
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry("test.txt");
            zos.putNextEntry(entry);
            zos.write("hello".getBytes());
            zos.closeEntry();
        }
        byte[] zipData = baos.toByteArray();

        // Mock detection
        when(mockStrategy.detect(zipData)).thenReturn("application/zip");
        when(mockStrategy.detect(any(byte[].class))).thenAnswer(invocation -> {
            byte[] arg = invocation.getArgument(0);
            if (arg == zipData) return "application/zip";
            return "text/plain"; // For the content inside
        });

        MimeDetectionResult result = fileMimeCore.process(zipData);

        // Zip has 1 file inside
        assertEquals("text/plain", result.getFormattedResult());
        assertEquals(1, result.getMimeTypes().size());
    }
}

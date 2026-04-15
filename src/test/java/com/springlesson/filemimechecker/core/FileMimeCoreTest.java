package com.springlesson.filemimechecker.core;

import com.springlesson.filemimechecker.core.model.MimeDetectionResult;
import com.springlesson.filemimechecker.core.strategy.MimeDetectionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @DisplayName("단일 파일 MIME 타입 탐지 검증")
    public void testProcessSingleFile() throws IOException {
        byte[] data = "plain text content".getBytes();
        when(mockStrategy.detect(data)).thenReturn("text/plain");

        MimeDetectionResult result = fileMimeCore.process(data);

        assertEquals("text/plain", result.getFormattedResult());
        assertEquals(1, result.getMimeTypes().size());
    }

    @Test
    @DisplayName("ZIP 파일 분석 및 내부 파일 MIME 탐지 검증")
    public void testProcessZipFile() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry("test_file.txt");
            zos.putNextEntry(entry);
            zos.write("content inside zip".getBytes());
            zos.closeEntry();
        }
        byte[] zipData = baos.toByteArray();

        when(mockStrategy.detect(any(byte[].class))).thenAnswer(invocation -> {
            byte[] input = invocation.getArgument(0);

            if (Arrays.equals(input, zipData)) {
                return "application/zip";
            }
            return "text/plain";
        });

        MimeDetectionResult result = fileMimeCore.process(zipData);

        assertEquals("initial:application/zip;text/plain", result.getFormattedResult());
        assertEquals(2, result.getMimeTypes().size());
        assertEquals("initial:application/zip", result.getMimeTypes().get(0));
        assertEquals("text/plain", result.getMimeTypes().get(1));
    }
}

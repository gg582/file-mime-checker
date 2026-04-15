package com.springlesson.filemimechecker.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MimeDetectionResultTest {

    @Test
    @DisplayName("MimeDetectionResult 빌더 및 게터 검증")
    public void testMimeDetectionResult() {
        List<String> types = Arrays.asList("text/plain", "application/pdf");
        MimeDetectionResult result = MimeDetectionResult.builder()
                .addMimeTypes(types)
                .build();

        assertEquals(2, result.getMimeTypes().size());
        assertTrue(result.getMimeTypes().contains("text/plain"));
        assertTrue(result.getMimeTypes().contains("application/pdf"));
        assertEquals("text/plain;application/pdf", result.getFormattedResult());
    }

    @Test
    @DisplayName("단일 MIME 타입 추가 검증")
    public void testAddSingleMimeType() {
        MimeDetectionResult result = MimeDetectionResult.builder()
                .addMimeType("image/png")
                .build();

        assertEquals(1, result.getMimeTypes().size());
        assertEquals("image/png", result.getMimeTypes().get(0));
    }
}

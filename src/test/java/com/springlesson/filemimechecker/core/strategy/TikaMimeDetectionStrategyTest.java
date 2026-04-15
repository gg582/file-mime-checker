package com.springlesson.filemimechecker.core.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TikaMimeDetectionStrategyTest {

    private TikaMimeDetectionStrategy strategy;

    @BeforeEach
    public void setUp() {
        strategy = new TikaMimeDetectionStrategy();
    }

    @Test
    @DisplayName("null 데이터일 때 기본 MIME 타입 반환 검증")
    public void testDetectWithNullData() {
        String result = strategy.detect(null);
        assertEquals("application/octet-stream", result);
    }

    @Test
    @DisplayName("빈 데이터일 때 기본 MIME 타입 반환 검증")
    public void testDetectWithEmptyData() {
        String result = strategy.detect(new byte[0]);
        assertEquals("application/octet-stream", result);
    }

    @Test
    @DisplayName("텍스트 데이터 MIME 타입 탐지 검증")
    public void testDetectWithTextData() {
        byte[] data = "This is a plain text file.".getBytes();
        String result = strategy.detect(data);
        assertEquals("text/plain", result);
    }

    @Test
    @DisplayName("PDF 데이터 MIME 타입 탐지 검증")
    public void testDetectWithPdfData() {
        // PDF magic number: %PDF-
        byte[] data = "%PDF-1.4".getBytes();
        String result = strategy.detect(data);
        assertEquals("application/pdf", result);
    }
}

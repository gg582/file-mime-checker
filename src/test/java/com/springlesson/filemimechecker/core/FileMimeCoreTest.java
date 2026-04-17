package com.springlesson.filemimechecker.core;

import com.springlesson.filemimechecker.core.model.MimeDetectionResult;
import com.springlesson.filemimechecker.core.strategy.MimeDetectionStrategy;
import com.springlesson.filemimechecker.core.strategy.TikaMimeDetectionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    @DisplayName("실제 Archive.zip 파일을 활용한 통합 탐지 검증")
    public void testProcessArchiveZipFromResources() throws IOException {
        // TikaMimeDetectionStrategy를 사용하는 실제 FileMimeCore 생성
        FileMimeCore realCore = new FileMimeCore(new TikaMimeDetectionStrategy());
        
        // src/test/resources/Archive.zip 읽기
        byte[] zipData;
        try (InputStream is = getClass().getResourceAsStream("/Archive.zip")) {
            zipData = Objects.requireNonNull(is).readAllBytes();
        }

        MimeDetectionResult result = realCore.process(zipData);

        // Archive.zip 내부에는 test.txt (text/plain)와 test.pdf (application/pdf)가 포함되어 있음
        // 결과 순서는 ZIP 엔트리 순서에 따름 (보통 압축한 순서)
        
        assertTrue(result.getMimeTypes().contains("initial:application/zip"));
        assertTrue(result.getMimeTypes().contains("text/plain"));
        assertTrue(result.getMimeTypes().contains("application/pdf"));
        
        // 전체 포맷팅 결과 확인 (순서는 보장되지 않을 수 있으므로 포함 여부로 확인하는 것이 좋지만, 
        // 여기서는 생성한 순서대로 text.txt, test.pdf 순일 가능성이 높음)
        String formatted = result.getFormattedResult();
        assertTrue(formatted.contains("initial:application/zip"));
        assertTrue(formatted.contains("text/plain"));
        assertTrue(formatted.contains("application/pdf"));
    }

    @Test
    @DisplayName("다른 ZIP MIME 타입(application/x-zip-compressed) 탐지 검증")
    public void testProcessZipWithAlternativeMime() throws IOException {
        byte[] data = "dummy zip data".getBytes();
        // Mockito는 마지막에 설정한 스텁이 우선순위를 가질 수 있으므로, 범용적인 설정을 먼저 하고 구체적인 설정을 나중에 함
        when(mockStrategy.detect(Mockito.any(byte[].class))).thenReturn("application/octet-stream");
        when(mockStrategy.detect(data)).thenReturn("application/x-zip-compressed");

        MimeDetectionResult result = fileMimeCore.process(data);

        assertTrue(result.getMimeTypes().contains("initial:application/x-zip-compressed"));
    }

    @Test
    @DisplayName("uncompressZippedByte 메서드 직접 검증 (Archive.zip 사용)")
    public void testUncompressZippedByteDirectly() throws IOException {
        byte[] zipData;
        try (InputStream is = getClass().getResourceAsStream("/Archive.zip")) {
            zipData = Objects.requireNonNull(is).readAllBytes();
        }

        java.util.List<byte[]> files = fileMimeCore.uncompressZippedByte(zipData);
        
        assertEquals(2, files.size());
    }

    @Test
    @DisplayName("ZIP 내 폴더가 포함된 경우 건너뛰기 검증")
    public void testUncompressZippedByteWithDirectory() throws IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos);
        
        // 폴더 엔트리 추가
        java.util.zip.ZipEntry dirEntry = new java.util.zip.ZipEntry("testDir/");
        zos.putNextEntry(dirEntry);
        zos.closeEntry();
        
        // 파일 엔트리 추가
        java.util.zip.ZipEntry fileEntry = new java.util.zip.ZipEntry("testDir/file.txt");
        zos.putNextEntry(fileEntry);
        zos.write("hello".getBytes());
        zos.closeEntry();
        
        zos.close();
        
        byte[] zipData = baos.toByteArray();
        java.util.List<byte[]> files = fileMimeCore.uncompressZippedByte(zipData);
        
        // 폴더는 무시되고 파일 1개만 추출되어야 함
        assertEquals(1, files.size());
        assertEquals("hello", new String(files.get(0)));
    }
}

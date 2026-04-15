package com.springlesson.filemimechecker.core;

import com.springlesson.filemimechecker.core.model.MimeDetectionResult;
import com.springlesson.filemimechecker.core.strategy.MimeDetectionStrategy;
import com.springlesson.filemimechecker.core.strategy.TikaMimeDetectionStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 파일의 MIME 타입을 검사하고 압축 파일을 처리하는 핵심 비즈니스 로직 클래스입니다.
 */
public class FileMimeCore {

    // 전략 패턴 인터페이스: 실제 판별을 수행할 객체를 참조
    private final MimeDetectionStrategy strategy;

    /**
     * 기본 생성자. 기본 전략으로 Apache Tika(TikaMimeDetectionStrategy)를 사용합니다.
     */
    public FileMimeCore() {
        this(new TikaMimeDetectionStrategy());
    }

    /**
     * 커스텀 전략을 주입받기 위한 생성자입니다. (테스트 또는 확장에 용이)
     * @param strategy 주입할 MIME 판별 전략 객체
     */
    public FileMimeCore(MimeDetectionStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 전달된 바이트 데이터를 처리하여 MIME 타입을 탐지합니다.
     * ZIP 형식의 파일일 경우 내부를 순회하며 개별 파일들의 타입을 탐지하여 결과에 추가합니다.
     *
     * @param data 분석할 파일 데이터
     * @return 빌더 패턴을 통해 생성된 MIME 탐지 결과 객체
     */
    public MimeDetectionResult process(byte[] data) throws IOException {
        MimeDetectionResult.Builder builder = MimeDetectionResult.builder();
        
        // 전체 파일의 1차 MIME 타입 탐지
        String initialMime = strategy.detect(data);
        
        // 파일이 ZIP 압축 파일인 경우 압축 해제 후 내부 파일 순회
        if ("application/zip".equals(initialMime) || "application/x-zip-compressed".equals(initialMime)) {
            builder.addMimeType("initial:"+ initialMime);
            List<byte[]> unzipped = uncompressZippedByte(data);
            for (byte[] fileBytes : unzipped) {
                // 내부 파일 각각에 대해 탐지 전략을 호출하여 빌더에 추가
                builder.addMimeType(strategy.detect(fileBytes));
            }
        } else {
            // 단일 파일인 경우 그대로 추가
            builder.addMimeType(initialMime);
        }
        
        return builder.build();
    }

    /**
     * ZIP 형태의 바이트 배열 메모리상에서 압축 해제하여 개별 파일들의 바이트 배열 리스트로 반환합니다.
     *
     * @param zipData ZIP 파일 원본 바이트 데이터
     * @return 압축 해제된 개별 파일의 바이트 데이터 리스트
     */
    public List<byte[]> uncompressZippedByte(byte[] zipData) throws IOException {
        List<byte[]> result = new ArrayList<>();

        ByteArrayInputStream bais = new ByteArrayInputStream(zipData);
        ZipInputStream zis = new ZipInputStream(bais);

        ZipEntry entry;
        byte[] buffer = new byte[8192];

        // ZIP 파일 내부의 엔트리(파일)를 하나씩 읽어들임
        while ((entry = zis.getNextEntry()) != null) {
            // 폴더는 무시
            if (entry.isDirectory()) {
                zis.closeEntry();
                continue;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read;
            while ((read = zis.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            result.add(baos.toByteArray());
            zis.closeEntry();
        }
        zis.close();
        return result;
    }
}

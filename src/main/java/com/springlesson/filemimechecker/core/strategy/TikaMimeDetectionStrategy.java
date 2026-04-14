package com.springlesson.filemimechecker.core.strategy;

import org.apache.tika.Tika;

/**
 * Apache Tika 라이브러리를 이용해 MIME 타입을 판별하는 구체적인 전략 클래스입니다.
 */
public class TikaMimeDetectionStrategy implements MimeDetectionStrategy {
    private final Tika tika = new Tika();

    /**
     * Tika를 사용하여 파일 내용 기반의 MIME 타입을 감지합니다.
     * 데이터가 비어있을 경우 기본값인 'application/octet-stream'을 반환합니다.
     */
    @Override
    public String detect(byte[] data) {
        if (data == null || data.length == 0) {
            return "application/octet-stream"; // 데이터가 없을 경우의 기본값
        }
        return tika.detect(data);
    }
}

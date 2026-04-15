package com.springlesson.filemimechecker.core.strategy;

/**
 * MIME 타입 탐지 알고리즘을 캡슐화하기 위한 전략 인터페이스입니다.
 * 전략 패턴을 적용하여, 탐지 방식이 변경되거나 추가될 때 기존 코드의 수정 없이 확장이 가능합니다.
 */
public interface MimeDetectionStrategy {
    /**
     * 바이트 배열 데이터를 분석하여 MIME 타입을 반환합니다.
     *
     * @param data 분석할 파일의 바이트 데이터
     * @return 판별된 MIME 타입 문자열 (예: "text/plain", "application/pdf")
     */
    String detect(byte[] data);
}

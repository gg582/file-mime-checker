package com.springlesson.filemimechecker.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MIME 탐지 결과를 담는 불변 객체입니다.
 * 빌더 패턴을 적용하여 단일 파일 및 압축 파일 내부의 다중 파일 결과를 
 * 안전하고 유연하게 조립할 수 있도록 설계되었습니다.
 */
public class MimeDetectionResult {
    private final List<String> mimeTypes;

    private MimeDetectionResult(Builder builder) {
        this.mimeTypes = builder.mimeTypes;
    }

    /**
     * 탐지된 모든 MIME 타입 리스트를 반환합니다.
     */
    public List<String> getMimeTypes() {
        return new ArrayList<>(mimeTypes);
    }

    /**
     * 탐지된 MIME 타입들을 세미콜론(;)으로 연결하여 하나의 문자열로 포맷팅합니다.
     */
    public String getFormattedResult() {
        return mimeTypes.stream().collect(Collectors.joining(";"));
    }

    /**
     * MimeDetectionResult 객체 생성을 위한 정적 빌더 클래스입니다.
     */
    public static class Builder {
        private final List<String> mimeTypes = new ArrayList<>();

        public Builder addMimeType(String mimeType) {
            this.mimeTypes.add(mimeType);
            return this;
        }

        public Builder addMimeTypes(List<String> mimeTypes) {
            this.mimeTypes.addAll(mimeTypes);
            return this;
        }

        public MimeDetectionResult build() {
            return new MimeDetectionResult(this);
        }
    }

    /**
     * 빌더 객체를 생성합니다.
     */
    public static Builder builder() {
        return new Builder();
    }
}

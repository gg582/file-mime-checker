package com.springlesson.filemimechecker.cli;

import com.springlesson.filemimechecker.core.FileMimeCore;
import com.springlesson.filemimechecker.core.model.MimeDetectionResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 터미널 환경(CLI 모드)에서 파일 처리를 담당하는 컨트롤러입니다.
 */
public class CLIFileMimeController {

    private final FileMimeCore core = new FileMimeCore();

    /**
     * 터미널에서 입력된 파일 경로를 읽어 MIME 타입을 반환합니다.
     * 
     * @param filePath 분석할 대상 파일의 경로
     * @return MIME 타입 검사 결과 문자열 (여러 개일 경우 세미콜론 구분)
     */
    public String CLIDetectFiletype(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        
        // 파일 존재 여부 검사
        if (!Files.exists(path)) {
            return "파일을 찾을 수 없습니다: " + filePath;
        }
        
        // 파일을 바이트 단위로 모두 읽어 Core 비즈니스 로직에 전달
        byte[] fileContent = Files.readAllBytes(path);
        MimeDetectionResult result = core.process(fileContent);

        // 정해진 포맷에 따라 문자열 반환
        return result.getFormattedResult();
    }
}

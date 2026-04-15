package com.springlesson.filemimechecker.web;

import com.springlesson.filemimechecker.core.FileMimeCore;
import com.springlesson.filemimechecker.core.model.MimeDetectionResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 서버 모드 구동 시 파일 업로드 및 분석 요청을 처리하는 REST API 컨트롤러입니다.
 */
@RestController
public class FileMimeController {

    private final FileMimeCore fileMimeCore = new FileMimeCore();

    /**
     * '/upload' 엔드포인트로 파일을 POST 전송받아 MIME 타입을 판별합니다.
     * 
     * @param file 업로드된 Multipart 데이터
     * @return MIME 타입 검사 결과 문자열 (여러 개일 경우 세미콜론 구분)
     */
    @RequestMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        byte[] data = file.getBytes();
        
        // Core 비즈니스 로직에 처리를 위임
        MimeDetectionResult result = fileMimeCore.process(data);
        
        // 정해진 포맷의 결과를 HTTP Response로 반환
        return result.getFormattedResult();
    }
}

package com.springlesson.filemimechecker;

import com.springlesson.filemimechecker.cli.CLIFileMimeController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

/**
 * File MIME Checker 애플리케이션의 메인 진입점 클래스입니다.
 * 실행 시 인자로 전달되는 옵션을 분석하여 CLI 모드 또는 서버 모드로 애플리케이션을 구동합니다.
 */
@SpringBootApplication
public class FileMimeCheckerApplication {

    public static void main(String[] args) throws Exception {
        List<String> argList = Arrays.asList(args);
        
        // '-d' 옵션이 주어지면 Spring Boot를 활용한 서버(REST API) 모드로 구동합니다.
        if (argList.contains("-d")) {
            System.out.println("서버 모드(REST API)로 시작합니다...");
            SpringApplication.run(FileMimeCheckerApplication.class, args);
        } else {
            // '-d' 옵션이 없다면 CLI(Command Line Interface) 모드로 구동합니다.
            if (args.length == 0) {
                System.out.println("사용법: java -jar file-mime-checker.jar [-d] [파일1] [파일2] ...");
                System.out.println("  -d: 서버 모드로 실행 (REST API 지원)");
                System.out.println("  [파일1] [파일2] ...: MIME 타입을 확인할 파일 경로 (CLI 모드)");
                return;
            }

            CLIFileMimeController controller = new CLIFileMimeController();
            for (String arg : args) {
                if (arg.equals("-d")) continue;
                
                String result = controller.CLIDetectFiletype(arg);
                System.out.println("파일: " + arg);
                System.out.println("결과: " + result);
                System.out.println("---------------------------------------------------");
            }
        }
    }
}

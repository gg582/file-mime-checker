# File MIME Checker

다양한 파일의 MIME 타입을 판별해주는 범용 유틸리티 프로그램입니다.
Apache Tika 라이브러리를 활용하여 정확한 파일 타입을 감지하며, ZIP 형태의 압축 파일이 주어질 경우 압축을 풀고 내부 파일들을 순회하여 각각의 MIME 타입을 반환합니다.

## 실행 모드 안내

본 프로그램은 학습 및 실무 목적에 맞춰 두 가지 실행 모드를 제공합니다.

1. CLI 모드 (기본): 터미널에서 애플리케이션을 실행하면서 파일 경로를 입력하면 즉시 결과를 확인합니다.
2. 서버 모드 (-d 옵션): Spring Boot 기반의 내장 웹 서버(REST API)가 구동되어 네트워크를 통해 파일 판별 요청을 처리합니다.

## 프로젝트 빌드 방법

터미널에서 Maven Wrapper를 사용하여 패키징을 진행합니다.

```bash
./mvnw clean package
```

빌드가 완료되면 target/ 디렉토리에 실행 가능한 jar 파일이 생성됩니다.

## 실행 방법

### 1. CLI 모드 (터미널 직접 확인)

```bash
java -jar target/file-mime-checker-0.0.1-SNAPSHOT.jar [파일경로1] [파일경로2] ...
```

### 2. 서버 모드 (REST API로 구동)
애플리케이션을 데몬(서버) 형태로 띄우려면 -d 옵션을 추가합니다.

```bash
java -jar target/file-mime-checker-0.0.1-SNAPSHOT.jar -d
```

서버가 구동된 후, http://localhost:8080/upload URL을 통해 POST 방식으로 multipart/form-data 형식의 file 파라미터를 전송하면 MIME 타입 결과를 반환받을 수 있습니다.

## 설계 포인트
- 전략 패턴(Strategy Pattern): 추후 탐지 알고리즘이 변경되어도 쉽게 적용할 수 있게 캡슐화 하였습니다.
- 빌더 패턴(Builder Pattern): 압축 파일 내의 여러 결과값을 안전하고 일관성 있게 구성하기 위해 활용하였습니다.

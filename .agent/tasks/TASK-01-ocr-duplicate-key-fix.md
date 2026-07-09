# TASK-01: OCR 약품 목록 중복 키 크래시 에러 방지 및 로컬 테스트 환경 구축

## 1. 목적 (Context)
- OCR 분석 결과 반환 데이터에 동일한 약품 이름이 포함되어 전달되는 경우, `OcrScreen` 내 `LazyColumn` 렌더링 도중 고유 키 중복 예외(`IllegalArgumentException`)가 발생하여 앱이 강제 종료됨.
- 컴포넌트가 어떠한 중복 상태의 데이터 목록을 받더라도 비정상 종료 없이 독립적으로 안전하게 렌더링하도록 UI 명세를 정의하고 로컬 UI 테스트를 통해 검증함.

## 2. 세부 구현 요구사항 (Todo)
- [x] 컴포넌트 격리 테스트를 위해 `OcrScreenContent`의 가시성을 `internal`로 격상
- [x] 중복 데이터 상황을 재현하고 방지하기 위해 로컬 UI 테스트(`OcrScreenTest.kt`) 작성
- [x] `OcrScreen.kt` 내의 `LazyColumn` key 설정을 index 조합식(`"${medicine.name}_$index"`)으로 안전하게 수정
- [x] 에뮬레이터 없이도 로컬에서 다양한 UI 데이터 상태를 디자이너와 소통하며 눈으로 볼 수 있게 컴포즈 프리뷰(`OcrScreenContentPreview`) 작성
- [x] (보완 작업) 메인 UI 로직 코드와 프리뷰의 관심사를 분리하기 위해 `OcrScreenContentPreview`를 별도 파일로 분리

## 3. 하네스 제약 조건 (Harness)
- 수정/생성해야 하는 파일:
  - 메인 코드: `OcrScreen.kt`
  - 프리뷰 코드: `OcrScreenPreview.kt`
  - 테스트 코드: `OcrScreenTest.kt`
- 검증 명령어:
  - 로컬 UI 테스트 실행: `./gradlew connectedAndroidTest`
  - CI 빌드 검증 실행: `./gradlew build`
- **규칙:** 로컬 및 CI 환경에서 위 검증 명령어가 완전히 성공(Green)하고, GitHub Actions에서 Node.js 관련 deprecation 경고가 발생하지 않는 최신 Action 환경(checkout@v6, setup-java@v5)을 충족해야 함.

## 4. 진행 현황 (Status)
- **로컬 검증 상태:** 로컬 테스트 및 프리뷰 분리 작업 모두 완료하여 검증 통과.
- **다음 할 일:**
  1. 수정된 변경 사항들을 피처 브랜치(`feature/ocr-duplicate-key-fix`)에 커밋 및 푸시.
  2. GitHub 저장소에서 `main` 브랜치를 향한 Pull Request(PR)를 발행하여 CodeRabbit AI 연동 및 자동 코드 리뷰 결과 확인.

# DigitalTok Android Front-end

## 1. 프로젝트 소개
DigitalTok은 사용자가 선택한 이미지를 전자잉크 기반 디바이스(그립톡)에 전송하여
개인화된 화면을 표시할 수 있도록 하는 모바일 애플리케이션입니다.

본 레포지토리는 DigitalTok 프로젝트의 Android Front-end 구현을 담당합니다.

### 주요 기능
- 사용자 갤러리에서 이미지 선택
- 선택한 이미지 전처리 및 미리보기 제공
- NFC 통신을 통한 전자잉크 디바이스(그립톡) 이미지 전송
- 디바이스 화면 꾸미기 기능 (이미지 기반 커스터마이징)
- FAQ / 고객지원 화면 제공
- 공용 상단바 UI 컴포넌트 적용

---

## 2. 실행 환경

### Development Environment
- Android Studio: Hedgehog 이상
- Language: Kotlin
- JDK: 17
- minSdk: 26
- targetSdk: 34

---

## 3. Code Convention
- Kotlin 파일: PascalCase
- 변수 / 함수명: PascalCase
- XML id: camelCase

---

## 4. Commit Convention

본 프로젝트는 초기 개발 단계에서 명확한 커밋 규칙 없이 작업이 진행되었습니다.
이후 협업 및 유지보수를 위해 아래와 같은 커밋 메시지 규칙을 정리하였습니다.

### Commit Type
- FEAT: 새로운 기능 추가
- FIX: 버그 수정
- CHORE: 설정, 빌드, 잡다한 작업
- DOCS: 문서 수정
- REFACTOR: 코드 리팩토링
  
---

## 5. Issue / PR Rule (Recommended)

- Issue를 통해 작업 단위를 관리하는 것을 권장합니다.
- PR 작성 시 변경 사항과 테스트 내용을 명시합니다.

### 1. 프로젝트 개요

* **프로젝트명:** ChessAssist MVP
* **목적:** 안드로이드 기기에서 체스닷컴 게임 화면을 캡처 → FEN 변환 → Stockfish 분석 → 오버레이로 최적 수 표시.
* **핵심 기능:**

  1. `CaptureManager` + `ScreenCaptureActivity` : MediaProjection 기반 주기적 화면 캡처
  2. `ChessvisionRepository` : chessvision.ai API 연동, 이미지→FEN 변환
  3. `StockfishRepository` : Stockfish 분석 API 호출, 최적 수 산출
  4. `OverlayService` : `TYPE_APPLICATION_OVERLAY`로 분석 결과 실시간 표시

---

### 2. 폴더 구조 (중요 디렉터리 기준)

```
.
├── app/                       # 안드로이드 앱 모듈
│   ├── build.gradle.kts
│   ├── src/main/java/com/example
│   │   ├── api/               # Chessvision·Stockfish API 연동
│   │   ├── capture/            # 화면 캡처 로직
│   │   ├── overlay/            # 오버레이 서비스
│   │   └── MainActivity.kt     # 앱 실행 및 루프 제어
│   └── src/main/res/           # 리소스 (UI, 문자열 등)
├── request/                    # Codex/GPT 요청 프롬프트 모음
├── docs/                       # 설계/다이어그램 자료
├── build.gradle.kts
└── settings.gradle.kts
```

---

### 3. 실행 방법

1. **사전 준비**

   * Android Studio 최신 버전
   * Android SDK 26 이상 (targetSdk 최신)
   * `local.properties`에 SDK 경로 설정
   * 테스트 기기에서 **USB 디버깅** 및 **Overlay 권한** 허용

2. **빌드 & 실행**

   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

   또는 Android Studio에서 **Run ▶** 클릭.

3. **권한 부여**

   * 최초 실행 시 화면 캡처 권한
   * Overlay 권한 (`SYSTEM_ALERT_WINDOW`)

---

### 4. 사용 흐름

1. 앱 실행 → 캡처 권한 요청
2. 주기적 화면 캡처 시작 (`CaptureManager`)
3. 이미지 → FEN 변환 (Chessvision API)
4. FEN → 최적 수 분석 (Stockfish API)
5. 오버레이 서비스(`OverlayService`)로 최적 수 표시

---

### 5. 향후 계획 / TODO

* [ ] Chessvision API 키 환경변수화 및 보안 처리
* [ ] Overlay UI 개선 (Jetpack Compose 적용, 화살표 렌더링)
* [ ] Stockfish 분석 결과에 따른 다중 후보 수 표시
* [ ] 네트워크 오류 시 재시도 로직 강화
* [ ] 단위 테스트 및 통합 테스트 추가

---

### 6. 라이선스

MIT / Apache 2.0 중 택일

---


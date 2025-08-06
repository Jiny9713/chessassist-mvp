# 🔄 전체 기능 통합 요청 (MVP 플로우 연결)

## 🎯 목적  
화면 캡처 → 이미지 전송 → FEN 추출 → Stockfish 분석 → best move 오버레이 표시  
이 일련의 흐름을 **5~10초 간격으로 반복 실행하는 전체 통합 코드**를 요청합니다.  
각 기능은 이미 별도 모듈로 구현되어 있다고 가정합니다.

---

## ✅ 요청 목적 요약

- `CaptureManager`를 통해 화면을 Bitmap으로 캡처
- `ChessvisionRepository`를 통해 이미지 → FEN 추출
- `StockfishRepository`를 통해 FEN → best move 분석
- `OverlayService`를 통해 화면에 텍스트로 표시
- 위 전체 과정을 **Coroutine + 반복 타이머**로 주기 실행
- 예외 발생 시에도 앱이 종료되지 않도록 안전 처리

---

## ⚙️ 요구 조건

1. `MainActivity` 또는 `MainViewModel` 등 중심 클래스에서 전체 흐름 제어
2. Coroutine을 활용하여 비동기 실행
3. 분석 주기: 기본 5초, 하드코딩 또는 조정 가능
4. 각 단계 실패 시 디버깅 로그 출력 및 다음 루프 유지
5. 텍스트 오버레이는 `updateOverlayText(text: String)` 함수를 호출하여 갱신

---

## 📁 구성 기대 구조

app/
├── MainActivity.kt ← 주기적 분석 트리거
├── capture/CaptureManager.kt
├── api/ChessvisionRepository.kt
├── api/StockfishRepository.kt
└── overlay/OverlayService.kt

- 추천 함수 구조 예시:

```kotlin
fun startAnalysisLoop(intervalMillis: Long = 5000L)
각 단계는 내부에서 try-catch로 감싸 예외 시 로그만 출력하고 루프 지속

---

###**📦 예상 실행 흐름**

```plaintext

[5초마다 실행]
→ CaptureManager.captureScreen()
→ ChessvisionRepository.extractFen(bitmap)
→ StockfishRepository.analyzeFen(fen)
→ OverlayService.updateOverlayText(bestMove)

---

###**🧪 테스트용 샘플 흐름 예시**
앱 실행 시 자동으로 분석 루프 시작

오버레이 텍스트: "Best move: e2e4" 와 같이 표시

로그캣에 각 단계별 진행 로그 출력

---

###**🔐 기타 고려사항**
앱이 종료되어도 백그라운드에서 계속 작동하려면 ForegroundService화 필요

단순 MVP 기준에서는 MainActivity 활성 상태에서만 루프 유지로 제한 가능

---

###**📎 참고 키워드 (Codex 참고용)**
Coroutine

Handler / Timer

repeat loop

try-catch

updateOverlayText

best move rendering

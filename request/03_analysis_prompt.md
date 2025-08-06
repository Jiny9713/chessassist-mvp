# ♟️ FEN → 최선 수 분석 기능 요청 (Stockfish 서버 연동)

## 🎯 목적  
chessvision API 또는 기타 방식으로 추출한 FEN 값을  
**Stockfish 분석 서버(Flask 또는 Firebase Functions 등)**에 전송하고,  
서버가 응답한 **best move (예: "e2e4")**를 앱 내부에서 사용 가능하도록 파싱하는 코드를 요청합니다.

---

## ✅ 요청 목적 요약

- 앱 내부에서 문자열 형태의 FEN을 서버로 전송
- 서버는 Stockfish를 통해 분석 후 JSON 응답을 반환
- 응답에서 `best_move`, `pv`, `evaluation` 등의 값을 추출
- 결과는 추후 오버레이 표시를 위해 저장

---

## ⚙️ 요구 조건

1. `Retrofit2` + `Coroutine` 기반 REST API 연동
2. 요청 Body는 JSON 형태, 예시:

```json
{
  "fen": "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1",
  "depth": 15,
  "multi_pv": 1
}

3. 응답예시:

```json
{
  "best_move": "e7e5",
  "pv": ["e7e5", "g1f3", "b8c6"],
  "evaluation": {
    "type": "cp",
    "value": -14
  }
}

4. 응답에서 best_move만 추출하여 사용할 수 있도록 가공
5. 실패 시 fallback 메시지 또는 로그 출력
6. 간단한 테스트용 suspend 함수 포함 (suspend fun analyzeFen(fen: String): String)

---

## **🧩 출력 기대 구조**
 - Kotlin 코드
 - 다음과 같은 클래스 및 파일 구조 권장:

```markdown

app/
└── api/
    ├── StockfishApi.kt            ← Retrofit 인터페이스
    └── StockfishRepository.kt     ← 분석 결과 추출 및 예외 처리

 - 응답 매핑용 data class AnalysisResponse(...) 포함
 - Authorization Header는 필요 시 주석 처리로 샘플 제공

---

## **🔐 권한 및 설정 필요**
android.permission.INTERNET

Retrofit, Coroutine 의존성 포함

앱 내에서 서버 URL을 교체 가능하도록 상수화

---

##**🔗 참고 API 엔드포인트 예시**
```http
POST https://your-stockfish-server.com/api/analyze
Content-Type: application/json

Request:
{
  "fen": "...",
  "depth": 15
}

Response:
{
  "best_move": "e2e4",
  "pv": ["e2e4", "e7e5"],
  "evaluation": { "type": "cp", "value": 34 }
}

---
## **참고키워드**

```kotlin
Retrofit2
POST JSON body
data class for API response
Coroutine suspend function
UCI best move




# 🧠 이미지 → FEN 추출 기능 요청 (chessvision.ai API 연동)

## 🎯 목적
안드로이드 앱에서 캡처한 체스 게임 화면(Bitmap 이미지)을 **chessvision.ai API**로 전송하여  
기물 배치 상태를 나타내는 **FEN(Forsyth–Edwards Notation)** 값을 추출하는 코드를 요청합니다.

---

## ✅ 요청 목적 요약

- CaptureManager를 통해 획득한 Bitmap 이미지를 서버로 전송
- 서버는 이미지 인식 후 FEN 값을 포함한 JSON 응답을 반환
- 응답에서 FEN 문자열을 파싱하고 이후 단계로 전달

---

## ⚙️ 요구 조건

1. `Retrofit2`를 사용한 REST API 연동
2. 이미지 전송 방식은 **Multipart/form-data** 형식
3. 응답 JSON에서 `fen` 필드 값을 추출
4. `Coroutine`을 사용하여 비동기 요청 처리
5. 분석 실패 시 적절한 예외 처리 및 로그 출력 포함
6. API 호출을 테스트할 수 있는 샘플 함수 포함
7. 실제 서버 호출 시 필요한 경우 `API Key`를 Header에 포함하는 방식도 고려

---

## 🧩 출력 기대 구조

- Kotlin 코드
- 다음과 같은 클래스 및 파일 구조 권장:

app/
└── api/
├── ChessvisionApi.kt ← Retrofit 인터페이스
└── ChessvisionRepository.kt ← API 호출 및 결과 처리


- `data class ChessvisionResponse(val fen: String)` 형태로 응답 매핑
- 예시용 suspend 함수: `suspend fun extractFenFromImage(bitmap: Bitmap): String`

---

## 🔐 권한 및 설정 필요

- `android.permission.INTERNET`
- Retrofit, OkHttp, Coroutine 의존성 포함
- (선택 사항) API Key를 Header에 포함하는 코드도 주석 처리하여 포함 가능

---

## 🔗 참고 API 엔드포인트 예시 (가정)

```http
POST https://api.chessvision.ai/v1/recognize
Headers:
  Authorization: Bearer YOUR_API_KEY
Body (multipart/form-data):
  image: [Bitmap 이미지 파일]
Response:
{
  "fen": "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1"
}

---

## **📎 참고 키워드 (Codex 참고용)**

Retrofit
Multipart
Bitmap to File
RequestBody
@Multipart
suspend function
chessvision.ai

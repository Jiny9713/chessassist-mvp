# 🪟 텍스트 오버레이 표시 기능 요청 (Android Overlay Service)

## 🎯 목적  
안드로이드 화면 상단에 Stockfish 분석 결과인 **best move 텍스트 (예: "e2e4")**를  
다른 앱 위에 **항상 보이도록 오버레이 형태로 표시**하는 Foreground Service 코드를 요청합니다.

> 화면 위치는 우측 상단 또는 하단 고정, 간결한 스타일의 텍스트 박스로 표시  
> 권한 요청, 뷰 제거, 텍스트 갱신 기능을 포함한 구조로 작성

---

## ✅ 요청 목적 요약

- 오버레이는 Android의 `TYPE_APPLICATION_OVERLAY`를 사용해 표시
- 텍스트는 반투명한 배경에 검정 글씨로 표시
- 앱 외부에서도 보이도록 설정
- 분석 결과가 갱신될 때 오버레이 텍스트도 갱신 가능해야 함
- ForegroundService 방식으로 구현하여 백그라운드에서도 유지

---

## ⚙️ 요구 조건

1. 오버레이를 별도의 `OverlayService` 클래스로 구현
2. 서비스 시작 시 텍스트가 오버레이로 표시됨
3. 서비스 중 텍스트 업데이트 가능 (`updateOverlayText("e2e4")` 같은 함수 포함)
4. 화면 우측 상단에 텍스트 박스 위치 고정
5. 단일 텍스트만 표시 (레이아웃은 간단하게 유지)
6. Foreground 알림 포함 (Android 8.0+ 이상 대응)

---

## 🧩 출력 기대 구조

- Kotlin 코드
- 다음 파일로 구성 권장:

app/
└── overlay/
└── OverlayService.kt

- 추가적으로 사용할 수 있는 함수 예시:
  - `startOverlayService(context)`
  - `stopOverlayService(context)`
  - `updateOverlayText(text: String)`

---

## 🛠️ UI 스타일 요구

- 텍스트: `"Best move: e2e4"` 같은 형식
- 배경: 반투명 흰색 박스 (`#F0FFFFFF` 정도)
- 글자 색상: 검정 (`#000000`)
- 텍스트 크기: 16sp 이상
- 패딩: 최소 8dp
- 위치: 우측 상단 (WindowManager.LayoutParams로 지정)

---

## 🔐 권한 및 설정 필요

- AndroidManifest에 다음 권한 추가:

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
최초 실행 시 Settings.canDrawOverlays(context) 확인 후 설정 페이지로 유도

---

##**📎 참고 키워드 (Codex 참고용)**
```sql
Overlay
ForegroundService
WindowManager
LayoutInflater
updateOverlayText
TextView
TYPE_APPLICATION_OVERLAY

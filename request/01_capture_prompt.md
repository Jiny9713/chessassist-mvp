# 📸 안드로이드 화면 캡처 기능 요청 (MediaProjection 기반)

## 🎯 목적
안드로이드 앱에서 체스닷컴 앱의 화면을 주기적으로 캡처하여 이미지(Bitmap)로 저장하기 위한 **MediaProjection 기반 코드**를 생성하고자 합니다.

---

## ✅ 요청 목적 요약

- 체스닷컴 앱이 실행 중일 때, 현재 화면을 일정 주기로 캡처
- 캡처된 이미지는 나중에 분석에 사용되므로 **Bitmap 형태로 저장**되어야 함
- 초기 권한 요청 및 사용자 승인 절차도 포함

---

## ⚙️ 요구 조건

1. Android 10 이상에서 동작 가능해야 함
2. MediaProjectionManager를 사용하여 화면 캡처 권한 요청
3. 사용자 승인 후, 일정 시간 간격(예: 5초)으로 화면을 자동 캡처
4. 캡처된 이미지를 Bitmap으로 반환하여 변수에 저장
5. UI 표시 및 파일 저장은 필요 없음 (내부 저장만)
6. 적절한 로그 출력을 통해 디버깅 용이하게 할 것

---

## 🧩 출력 기대 형식

- Kotlin 언어로 작성
- 다음과 같은 클래스 또는 구조로 분리 권장:
  - `CaptureManager.kt`: 화면 캡처 전담 클래스
  - 필요한 경우 Activity 내 처리 예시도 포함
- Android 권한 관련 코드 포함 (`MediaProjection`, `ImageReader` 등)
- 필요 권한 및 설정 사항도 주석으로 명시

---

## 🔐 권한 요구 사항

- `android.permission.FOREGROUND_SERVICE`
- `MediaProjectionManager` 시스템 서비스 사용
- AndroidManifest 및 사용자 승인 절차 포함

---

## 📁 연동 예정 위치

이 코드는 프로젝트 내 다음 위치에 연동됩니다:

app/
└── capture/
└── CaptureManager.kt

---

## 📎 참고 키워드 (Codex 참고용)

MediaProjection
VirtualDisplay
ImageReader
Surface
HandlerThread
Android screen capture

---

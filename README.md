# ♟ ChessAssist MVP (체스닷컴 보조 분석 앱)

이 프로젝트는 **체스닷컴 앱에서 체스 게임을 진행 중일 때, 화면을 자동 캡처하고 FEN을 추출하여 Stockfish 엔진으로 분석한 뒤, 최선의 수(best move)를 오버레이 텍스트로 표시하는 안드로이드 MVP 앱**입니다.

> 개인 실험 및 학습 목적이며, 외부 배포 또는 사용자 인증 기능은 포함하지 않습니다.

---

## 🎯 프로젝트 목표

- 체스닷컴 안드로이드 앱의 게임 화면을 일정 간격으로 캡처
- 이미지에서 FEN(Forsyth–Edwards Notation) 추출
- Stockfish 엔진을 통한 최선 수 분석
- 분석 결과를 **텍스트 형태의 오버레이**로 화면에 표시

---

## 🧩 기능 흐름도

[체스닷컴 게임 화면]
↓ (화면 캡처)
[이미지 → FEN 추출]
↓
[FEN → Stockfish 분석]
↓
[Best move → 텍스트 오버레이 표시]


---

## 🗂 디렉토리 구조 안내

| 폴더 | 설명 |
|------|------|
| `app/` | 기능별 안드로이드 코드 (화면 캡처, API 연동, 오버레이 등) |
| `request/` | Codex에게 요청할 프롬프트 파일(.md) 모음 |
| `docs/` | 전체 흐름 다이어그램 등 부가 자료 (선택 사항) |
| `.gitignore` | 안드로이드 빌드 파일 무시 설정 |
| `README.md` | 본 프로젝트 설명서 |

---

## 🛠️ 사용 기술 스택

- **언어**: Kotlin (Android)
- **화면 캡처**: `MediaProjectionManager`
- **이미지 업로드**: Retrofit2 + Coroutine
- **FEN 추출**: [chessvision.ai API](https://chessvision.ai)
- **수 분석**: Stockfish API 서버 (Flask or Firebase Function)
- **오버레이**: `TYPE_APPLICATION_OVERLAY` 사용
- **권한 처리**: MediaProjection + 오버레이 권한 수동 허용

---

## ⚠️ MVP 제한 사항

- 체스보드 테마는 고정되어야 함 (스킨 변경 시 인식 오류 가능)
- 화살표 렌더링 미포함 (텍스트 기반 오버레이만 제공)
- 인터넷 연결 필요 (API 기반 처리)
- 오직 1인 사용을 전제로 하며, 배포용 기능 없음

---

## 🪄 Codex와의 협업 방식

- 각 기능은 `request/` 폴더 내 `.md` 파일로 정리되어 있으며,  
  해당 내용을 ChatGPT 또는 Codex 입력창에 붙여 넣으면 자동 코드 생성 요청이 가능합니다.

| 파일명 | 설명 |
|--------|------|
| `01_capture_prompt.md` | 화면 캡처 기능 요청 |
| `02_fen_prompt.md` | 이미지 → FEN 추출 요청 |
| `03_analysis_prompt.md` | FEN → 수 분석 요청 |
| `04_overlay_prompt.md` | 텍스트 오버레이 요청 |
| `05_integration_prompt.md` | 전체 흐름 통합 요청 |

---

## 📝 개발 진행 상황

| 단계 | 상태 |
|------|------|
| 화면 캡처 | ✅ 완료 예정 |
| 이미지 전송 및 FEN 추출 | ✅ 예정 |
| Stockfish 분석 API 연동 | ✅ 예정 |
| 오버레이 텍스트 표시 | ✅ 예정 |
| 전체 통합 및 반복 실행 | ⏳ 개발 예정 |

---

## 📜 라이선스

- 본 프로젝트는 개인 실험 및 테스트용이며, 별도의 라이선스 없이 사용됩니다.
- 오픈소스 사용 시 해당 라이브러리의 라이선스를 반드시 따릅니다.


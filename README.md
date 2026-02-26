# Capsy

`cp`(체크포인트)와 `day`(하루 마감)로 작업 증거를 로컬 파일에 쌓는 Java CLI 도구.

---

## Quick Start

```bash
./gradlew :capsy-cli:installDist
export PATH="$PATH:$(pwd)/capsy-cli/build/install/capsy/bin"
capsy init && capsy cp "첫 체크포인트" && capsy day "오늘 마감"
```

---

## Why Capsy

LLM 채팅창에 요약을 맡기면 **기억이 아닌 추정**에 의존하게 된다.
Capsy는 `cp`/`day`로 기록을 먼저 쌓고, LLM은 v0.2에서 그 로그 위에 얹는 **선택 엔진**이다.
특정 LLM 벤더에 종속되지 않는 로컬 하네스가 핵심 정체성이다.

---

## 명령어

```bash
capsy init          # 현재 디렉토리에 .capsy/ 초기화
capsy cp <메시지>   # 작업 중간 체크포인트 기록
capsy day <메시지>  # 하루 마감 기록
capsy help          # 도움말
```

**예시:**

```bash
capsy cp 로그인 API 구현 완료
capsy day 오늘 API 3개 완성, 내일 테스트 작성 예정
```

**worklog 출력 (`.capsy/worklog/YYYY-MM-DD.md`):**

```
# 2026-02-26

> Capsy worklog

- [14:30] CHECKPOINT: 로그인 API 구현 완료
- [18:00] ENDDAY: 오늘 API 3개 완성, 내일 테스트 작성 예정
```

---

## .capsy 데이터 레이아웃

`capsy init` 실행 시 현재 디렉토리 아래에 생성된다.

```
.capsy/
├── tasks.md                          할 일 목록 (직접 관리)
├── worklog/
│   └── YYYY-MM-DD.md                 날짜별 cp/day 기록
└── prompts/
    ├── checkpoint.user.txt           cp 요약 프롬프트 (편집 가능)
    └── endday.user.txt               day 요약 프롬프트 (편집 가능)
```

`prompts/` 안의 파일은 사용자가 직접 편집할 수 있다. v0.2에서 LLM 연동 시 이 파일이 주입 템플릿으로 사용된다.

---

## 모듈 구조

```
capsy/
├── capsy-core/   파일 시스템 로직 (CapsyInitializer, WorklogWriter)
└── capsy-cli/    CLI 진입점 (CapsyApp)
```

상세 설계 → [`docs/v0.1.md`](docs/v0.1.md)

---

## Status & Roadmap

| 버전 | 상태 | 내용 |
|------|------|------|
| v0.1 | ✅ released | 로컬 worklog 하네스. `cp`/`day` → `.capsy/worklog/` |
| v0.2 | 🔲 planned | LLM 브리지 (벤더 중립), `.capsy/runs/` 실행 기록, `next` 명령 |

---

## 요구사항

- Java 21+
- Gradle Wrapper 포함 (`./gradlew` 사용 가능)

---

## 개발자용

```bash
# 테스트
./gradlew :capsy-core:test

# 빌드 (installDist)
./gradlew :capsy-cli:installDist
# 결과물: capsy-cli/build/install/capsy/bin/capsy

# PATH 영구 등록 (~/.zshrc 또는 ~/.bashrc)
export PATH="$PATH:$(pwd)/capsy-cli/build/install/capsy/bin"
```

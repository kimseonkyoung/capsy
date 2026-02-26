# Capsy

작업 중간 체크포인트(`cp`)와 하루 마감 정리(`day`)를 로컬 파일에 기록하는 Java CLI 도구.

LLM 프롬프트용 worklog 하네스로 설계됨 — `.capsy/worklog/` 에 날짜별 `.md` 파일로 쌓임.

---

## 설치 (installDist)

```bash
# 1) 빌드
./gradlew :capsy-cli:installDist

# 2) PATH 등록 (레포 루트에서 실행)
export PATH="$PATH:$(pwd)/capsy-cli/build/install/capsy/bin"

# ~/.zshrc 또는 ~/.bashrc 에 추가하면 영구 적용
```

---

## 사용법

```bash
capsy init          # 현재 디렉토리에 .capsy/ 초기화
capsy cp <메시지>   # 작업 중간 체크포인트 기록
capsy day <메시지>  # 하루 마감 기록
capsy help          # 도움말
```

**예시:**

```bash
capsy init
capsy cp IntelliJ 멀티모듈 셋업 완료
capsy day 오늘 init + 구조 + 푸시까지 완료
```

**worklog 출력 예시 (`.capsy/worklog/2026-02-26.md`):**

```markdown
# 2026-02-26

> Capsy worklog

- [14:30] CHECKPOINT: IntelliJ 멀티모듈 셋업 완료
- [18:00] ENDDAY: 오늘 init + 구조 + 푸시까지 완료
```

---

## 구조

```
capsy/
├── capsy-core/   파일 시스템 로직 (CapsyInitializer, WorklogWriter)
└── capsy-cli/    CLI 진입점 (CapsyApp)
```

상세 설계 → [`docs/v0.1.md`](docs/v0.1.md)

---

## 요구사항

- Java 21+
- Gradle (Wrapper 포함, `./gradlew` 사용)

---

## 상태

`v0.1` — 로컬 worklog 기록 완성. LLM 연동은 v0.2 예정.

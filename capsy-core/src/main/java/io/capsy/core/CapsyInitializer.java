package io.capsy.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Capsy 작업 디렉토리(.capsy)를 초기화하는 책임을 가진 클래스.
 *
 * 역할:
 * - .capsy 폴더 및 하위 디렉토리(worklog, prompts, schemas, runs) 생성
 * - 기본 파일(tasks.md, 사용자 프롬프트 파일) 생성
 *
 * 왜 core에 있나?
 * - CLI 출력(System.out)과 파일 시스템 로직을 분리하기 위해
 * - 나중에 다른 진입점(테스트, 다른 UI)에서도 재사용 가능하게 하기 위해
 */
public class CapsyInitializer {

    /**
     * workingDir(현재 작업 폴더)를 기준으로 .capsy 디렉토리를 초기화한다.
     *
     * 예)
     * workingDir = /Users/me/projects/ohaeng
     * 생성 대상 = /Users/me/projects/ohaeng/.capsy
     *
     * 출력(System.out)은 여기서 하지 않는다.
     * core는 파일 시스템 책임만 가지며, 메시지 출력은 CLI 진입점(CapsyApp)이 담당한다.
     */
    public void init(Path workingDir) throws IOException {
        Path root = workingDir.resolve(".capsy");

        // 1) 디렉토리 구조 생성
        // createDirectories는 "이미 있어도 예외 없이" 통과하므로 초기화에 안전(idempotent)
        Files.createDirectories(root);
        Files.createDirectories(root.resolve("worklog"));
        Files.createDirectories(root.resolve("prompts"));
        Files.createDirectories(root.resolve("schemas"));
        Files.createDirectories(root.resolve("runs"));

        // 2) 기본 파일 생성 (없을 때만 → 사용자가 수정한 파일을 덮어쓰지 않음)
        createFileIfAbsent(
                root.resolve("tasks.md"),
                "# Capsy Tasks\n"
                        + "\n"
                        + "## Backlog\n"
                        + "- [ ] 예: capsy cp/day 실제 저장 구현\n"
                        + "\n"
                        + "## Next\n"
                        + "- [ ] 예: worklog 포맷 개선\n"
        );

        createFileIfAbsent(
                root.resolve("prompts").resolve("checkpoint.user.txt"),
                "# Capsy Checkpoint Prompt (User Editable)\n"
                        + "\n"
                        + "아래 작업 로그를 바탕으로 중간 정리해줘.\n"
                        + "- 지금까지 한 일\n"
                        + "- 남은 일\n"
                        + "- 다음 액션 3개\n"
                        + "\n"
                        + "작업 로그:\n"
                        + "{{WORKLOG_SNIPPET}}\n"
        );

        createFileIfAbsent(
                root.resolve("prompts").resolve("endday.user.txt"),
                "# Capsy Endday Prompt (User Editable)\n"
                        + "\n"
                        + "아래 작업 로그를 바탕으로 오늘 작업을 정리해줘.\n"
                        + "- 오늘 완료한 것\n"
                        + "- 문제/막힌 점\n"
                        + "- 내일 첫 작업 3개\n"
                        + "\n"
                        + "작업 로그:\n"
                        + "{{WORKLOG_SNIPPET}}\n"
        );
    }

    /**
     * 파일이 없을 때만 생성하고 기본 내용을 넣는다.
     *
     * 왜 이 패턴을 쓰나?
     * - init을 여러 번 실행해도 기존 사용자 파일을 덮어쓰지 않기 위해 (idempotent)
     */
    private void createFileIfAbsent(Path path, String content) throws IOException {
        if (Files.notExists(path)) {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, content, StandardCharsets.UTF_8);
        }
    }
}
package io.capsy.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Capsy 작업 디렉토리(.capsy)를 초기화하는 책임을 가진 클래스.
 *
 * 역할:
 * - .capsy 폴더 생성
 * - 하위 디렉토리(worklog, prompts, schemas, runs) 생성
 * - 기본 파일(tasks.md, 사용자 프롬프트 파일) 생성
 *
 * 왜 core에 있나?
 * - CLI(ui)와 실제 파일 시스템 로직을 분리하기 위해
 * - 나중에 다른 진입점(테스트, 다른 UI)에서도 재사용 가능하게 하기 위해
 */
public class CapsyInitializer {

    /**
     * workingDir(현재 작업 폴더)를 기준으로 .capsy 디렉토리를 초기화한다.
     *
     * 예)
     * workingDir = /Users/me/projects/ohaeng
     * 생성 대상 = /Users/me/projects/ohaeng/.capsy
     */
    public void init(Path workingDir) throws IOException {
        Path root = workingDir.resolve(".capsy");

        // 1) 디렉토리 구조 생성
        // createDirectories는 "이미 있어도 예외 없이" 통과하므로 초기화에 안전함
        Files.createDirectories(root);
        Files.createDirectories(root.resolve("worklog"));
        Files.createDirectories(root.resolve("prompts"));
        Files.createDirectories(root.resolve("schemas"));
        Files.createDirectories(root.resolve("runs"));

        // 2) 기본 파일 생성 (없을 때만 생성)
        // 이미 사용자가 내용 수정한 파일이 있으면 덮어쓰지 않음
        createFileIfAbsent(root.resolve("tasks.md"), "");

        createFileIfAbsent(
                root.resolve("prompts/checkpoint.user.txt"),
                "# Optional user guidance for checkpoint summaries\n"
        );

        createFileIfAbsent(
                root.resolve("prompts/endday.user.txt"),
                "# Optional user guidance for end-of-day summaries\n"
        );

        System.out.println("[Capsy] Initialized at: " + root.toAbsolutePath());
    }

    /**
     * 파일이 없을 때만 생성하고 기본 내용을 넣는다.
     *
     * 왜 이 패턴을 쓰나?
     * - init을 여러 번 실행해도 기존 사용자 파일을 덮어쓰지 않기 위해
     * - idempotent(멱등적)하게 동작하게 만들기 위해
     */
    private void createFileIfAbsent(Path path, String content) throws IOException {
        if (Files.notExists(path)) {
            // 부모 폴더가 없으면 먼저 생성
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            // UTF-8로 문자열 파일 작성
            Files.writeString(path, content, StandardCharsets.UTF_8);
        }
    }
}
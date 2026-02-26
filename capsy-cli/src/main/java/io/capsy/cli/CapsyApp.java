package io.capsy.cli;

import io.capsy.core.CapsyInitializer;
import io.capsy.core.WorklogWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CapsyApp {

    public static void main(String[] args) {
        CapsyApp app = new CapsyApp();
        int exitCode = app.run(args);
        System.exit(exitCode);
    }

    public int run(String[] args) {
        try {
            CommandRequest request = parse(args);

            switch (request.command) {
                case "init":
                    return handleInit();

                case "cp":
                    return handleCheckpoint(request.message);

                case "day":
                    return handleEndday(request.message);

                case "help":
                case "-h":
                case "--help":
                    printUsage();
                    return 0;

                default:
                    System.err.println("❌ 알 수 없는 명령어: " + request.command);
                    printUsage();
                    return 1;
            }
        } catch (IllegalArgumentException e) {
            // 사용자 입력 오류 (빈 메시지 등) → 사용법 안내
            System.err.println("❌ 입력 오류: " + e.getMessage());
            printUsage();
            return 1;
        } catch (IllegalStateException e) {
            // 잘못된 상태 (init 미실행 등) → 사용법 불필요, 메시지만
            System.err.println("❌ " + e.getMessage());
            return 1;
        } catch (IOException e) {
            // 파일 시스템 오류
            System.err.println("❌ 파일 처리 오류: " + e.getMessage());
            return 2;
        } catch (Exception e) {
            // 예상 못한 오류 (v0.1에서는 메시지만)
            System.err.println("❌ 예상치 못한 오류: " + e.getMessage());
            return 9;
        }
    }

    /**
     * CLI 인자를 파싱해서 command + message 구조로 변환
     */
    private CommandRequest parse(String[] args) {
        if (args == null || args.length == 0) {
            return new CommandRequest("help", "");
        }

        String command = args[0].trim().toLowerCase();

        // 메시지가 필요한 명령어(cp/day)만 뒤 인자를 합침
        if ("cp".equals(command) || "day".equals(command)) {
            String message = joinArgs(args, 1);
            if (message.trim().isEmpty()) {
                throw new IllegalArgumentException("'" + command + "' 명령에는 메시지가 필요합니다.");
            }
            return new CommandRequest(command, message);
        }

        // init/help는 메시지 없어도 됨
        return new CommandRequest(command, "");
    }

    private int handleInit() throws IOException {
        Path root = resolveProjectRoot();
        new CapsyInitializer().init(root);   // 파일 시스템 로직은 core에 위임

        System.out.println("✅ Capsy 초기화 완료");
        System.out.println("   root: " + root);
        System.out.println("   created/checked: .capsy/, .capsy/prompts/, .capsy/worklog/, .capsy/tasks.md");
        return 0;
    }

    private int handleCheckpoint(String message) throws IOException {
        Path root = resolveProjectRoot();
        requireInitialized(root);
        WorklogWriter writer = new WorklogWriter(root);

        Path savedFile = writer.appendCheckpoint(message);

        System.out.println("✅ checkpoint 저장 완료");
        System.out.println("   file: " + savedFile);
        return 0;
    }

    private int handleEndday(String message) throws IOException {
        Path root = resolveProjectRoot();
        requireInitialized(root);
        WorklogWriter writer = new WorklogWriter(root);

        Path savedFile = writer.appendEndday(message);

        System.out.println("✅ endday 저장 완료");
        System.out.println("   file: " + savedFile);
        return 0;
    }

    /**
     * 실행 위치 기준 루트 경로 계산
     * (Gradle run의 workingDir를 rootProject로 맞췄다는 전제)
     */
    private Path resolveProjectRoot() {
        return Path.of("").toAbsolutePath().normalize();
    }

    /**
     * cp/day 실행 전 .capsy 초기화 여부 확인.
     *
     * 왜 IllegalStateException인가?
     * - IllegalArgumentException: 사용자 입력값이 잘못됐을 때
     * - IllegalStateException:    입력은 맞지만 실행 가능한 상태가 아닐 때 (init 미실행)
     */
    private void requireInitialized(Path root) {
        if (!Files.isDirectory(root.resolve(".capsy"))) {
            throw new IllegalStateException(
                    "Capsy가 초기화되지 않았습니다. 먼저 'capsy init'을 실행하세요.\n"
                    + "   대상 경로: " + root
            );
        }
    }

    /**
     * args[startIndex..] 를 공백으로 이어붙임
     * 예: ["cp", "오늘", "셋업", "완료"] -> "오늘 셋업 완료"
     */
    private String joinArgs(String[] args, int startIndex) {
        if (args == null || startIndex >= args.length) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            if (i > startIndex) {
                sb.append(' ');
            }
            sb.append(args[i]);
        }
        return sb.toString();
    }

    private void printUsage() {
        System.out.println();
        System.out.println("Capsy v0.1 (LLM-ready worklog helper)");
        System.out.println();
        System.out.println("사용법:");
        System.out.println("  capsy init");
        System.out.println("  capsy cp  <메시지>");
        System.out.println("  capsy day <메시지>");
        System.out.println("  capsy help");
        System.out.println();
        System.out.println("예시:");
        System.out.println("  capsy cp  IntelliJ/Gradle 멀티모듈 셋업 완료");
        System.out.println("  capsy day 오늘은 init + 구조 + 푸시까지 완료");
        System.out.println();
    }

    /**
         * command + message 구조를 명확히 하기 위한 내부 DTO
         */
        private record CommandRequest(String command, String message) {
    }
}
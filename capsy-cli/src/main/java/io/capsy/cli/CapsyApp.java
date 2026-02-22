package io.capsy.cli;

import io.capsy.core.CapsyInitializer;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Capsy CLI의 진입점(main) 클래스.
 *
 * 역할:
 * - 사용자 입력(args)을 받고
 * - 명령어(init/cp/day/next/open)를 분기 처리
 * - core 로직 호출
 *
 * 현재 단계(v0.1 초반):
 * - init만 실제 동작
 * - cp/day/next/open은 stub(메시지 출력)로 준비
 */
public class CapsyApp {

    public static void main(String[] args) {
        try {
            new CapsyApp().run(args);
        } catch (Exception e) {
            // 초반 단계라 단순 에러 출력
            // 나중에 커스텀 예외/에러코드/로그 구조로 발전 가능
            System.err.println("[Capsy][ERROR] " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 전체 명령어 라우팅 담당 메서드.
     */
    private void run(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0];

        // Java 14+ switch expression 스타일 (네가 Java 주력이라 읽기 좋을 것)
        switch (command) {
            case "init" -> handleInit();
            case "cp"   -> handleCheckpoint(Arrays.copyOfRange(args, 1, args.length));
            case "day"  -> handleEndday(Arrays.copyOfRange(args, 1, args.length));
            case "next" -> handleNext();
            case "open" -> handleOpen(Arrays.copyOfRange(args, 1, args.length));
            case "help", "--help", "-h" -> printUsage();
            default -> {
                System.out.println("[Capsy] Unknown command: " + command);
                printUsage();
            }
        }
    }

    /**
     * capsy init
     *
     * 현재 작업 디렉토리(cwd)를 기준으로 .capsy 초기화.
     *
     * Path.of("") 의미:
     * - "현재 실행 중인 디렉토리"를 Path로 가져옴
     * - capsy를 어떤 프로젝트 루트에서 실행하느냐가 중요하므로 적합함
     */
    private void handleInit() throws Exception {
        Path cwd = Path.of("").toAbsolutePath().normalize();
        new CapsyInitializer().init(cwd);
    }

    /**
     * capsy cp <memo...>
     *
     * 아직 stub:
     * - 나중에 중간 체크포인트를 worklog에 append
     * - LLM/fallback 요약 파이프라인 연결 예정
     */
    private void handleCheckpoint(String[] memoArgs) {
        String memo = String.join(" ", memoArgs).trim();

        if (memo.isBlank()) {
            System.out.println("[Capsy][cp] memo is empty (stub)");
            return;
        }

        System.out.println("[Capsy][cp] checkpoint memo (stub): " + memo);
    }

    /**
     * capsy day <memo...>
     *
     * 아직 stub:
     * - 나중에 End-of-day 요약 생성
     * - tasks.md 갱신 (다음 세션용)
     */
    private void handleEndday(String[] memoArgs) {
        String memo = String.join(" ", memoArgs).trim();

        if (memo.isBlank()) {
            System.out.println("[Capsy][day] memo is empty (stub)");
            return;
        }

        System.out.println("[Capsy][day] endday memo (stub): " + memo);
    }

    /**
     * capsy next
     *
     * 아직 stub:
     * - .capsy/tasks.md 읽어서 Next 3 출력 예정
     */
    private void handleNext() {
        System.out.println("[Capsy][next] TODO: read .capsy/tasks.md and print Next 3");
    }

    /**
     * capsy open tasks|today
     *
     * 아직 stub:
     * - tasks.md 또는 오늘 worklog 파일을 여는 동작 예정
     * - OS별 open 명령 처리(ProcessBuilder)로 확장 가능
     */
    private void handleOpen(String[] args) {
        if (args.length == 0) {
            System.out.println("[Capsy][open] usage: capsy open tasks|today");
            return;
        }

        String target = args[0];

        if ("tasks".equals(target)) {
            System.out.println("[Capsy][open] TODO: open .capsy/tasks.md");
        } else if ("today".equals(target)) {
            String today = LocalDate.now().toString();
            System.out.println("[Capsy][open] TODO: open .capsy/worklog/" + today + ".md");
        } else {
            System.out.println("[Capsy][open] unknown target: " + target);
        }
    }

    /**
     * CLI 도움말 출력
     */
    private void printUsage() {
        System.out.println("""
                Capsy - developer work capsule CLI (v0.1 alpha)

                Usage:
                  capsy init
                  capsy cp <memo...>
                  capsy day <memo...>
                  capsy next
                  capsy open tasks|today
                """);
    }
}
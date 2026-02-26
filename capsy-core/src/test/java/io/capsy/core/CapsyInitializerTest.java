package io.capsy.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CapsyInitializerTest {

    /**
     * init 실행 후 .capsy 하위에 필수 파일이 생성되는지 확인.
     *
     * @TempDir : JUnit 5가 테스트용 임시 디렉토리를 만들어 주입해줌.
     *            테스트 종료 시 자동으로 삭제되므로 실제 파일 시스템을 오염시키지 않음.
     */
    @Test
    void init_creates_tasks_and_prompts(@TempDir Path tempDir) throws IOException {
        new CapsyInitializer().init(tempDir);

        Path capsyDir = tempDir.resolve(".capsy");

        assertTrue(Files.exists(capsyDir.resolve("tasks.md")));
        assertTrue(Files.exists(capsyDir.resolve("prompts").resolve("checkpoint.user.txt")));
        assertTrue(Files.exists(capsyDir.resolve("prompts").resolve("endday.user.txt")));
    }
}

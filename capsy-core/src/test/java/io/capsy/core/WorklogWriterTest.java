package io.capsy.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WorklogWriterTest {

    /**
     * appendCheckpoint 호출 후 오늘 날짜 worklog 파일에
     * CHECKPOINT 라인이 추가되는지 확인.
     */
    @Test
    void appendCheckpoint_writes_line_to_worklog(@TempDir Path tempDir) throws IOException {
        WorklogWriter writer = new WorklogWriter(tempDir);
        writer.appendCheckpoint("셋업 완료");

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Path worklogFile = tempDir.resolve(".capsy").resolve("worklog").resolve(today + ".md");

        assertTrue(Files.exists(worklogFile));

        String content = Files.readString(worklogFile);
        assertTrue(content.contains("CHECKPOINT"));
        assertTrue(content.contains("셋업 완료"));
    }
}

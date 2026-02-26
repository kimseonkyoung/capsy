package io.capsy.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WorklogWriter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final Path projectRoot;

    public WorklogWriter(Path projectRoot) {
        if (projectRoot == null) {
            throw new IllegalArgumentException("projectRoot must not be null");
        }
        this.projectRoot = projectRoot;
    }

    public Path appendCheckpoint(String message) throws IOException {
        return appendEntry(EntryType.CHECKPOINT, message);
    }

    public Path appendEndday(String message) throws IOException {
        return appendEntry(EntryType.ENDDAY, message);
    }

    private Path appendEntry(EntryType entryType, String rawMessage) throws IOException {
        String message = normalizeAndValidate(rawMessage);

        Path worklogFile = resolveTodayWorklogFile();
        ensureParentDirectory(worklogFile);
        initializeFileIfAbsent(worklogFile);

        String entry = formatEntry(entryType, message);

        Files.writeString(
                worklogFile,
                entry,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );

        return worklogFile;
    }

    private Path resolveTodayWorklogFile() {
        String fileName = LocalDate.now().format(DATE_FORMAT) + ".md";

        return projectRoot
                .resolve(".capsy")
                .resolve("worklog")
                .resolve(fileName);
    }

    private void ensureParentDirectory(Path filePath) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /**
     * 파일이 없을 때만 헤더 생성
     */
    private void initializeFileIfAbsent(Path worklogFile) throws IOException {
        if (Files.exists(worklogFile)) {
            return;
        }

        String header = buildInitialHeader();
        Files.writeString(
                worklogFile,
                header,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE
        );
    }

    /**
     * v0.1은 단순 헤더만 넣음.
     * (다음 단계에서 섹션 기반으로 확장 가능)
     */
    private String buildInitialHeader() {
        String today = LocalDate.now().format(DATE_FORMAT);

        return "# " + today + "\n"
                + "\n"
                + "> Capsy worklog\n"
                + "\n";
    }

    /**
     * 한 줄 포맷 append (v0.1)
     * 예: - [19:42] CHECKPOINT: IntelliJ 셋업 완료
     */
    private String formatEntry(EntryType type, String message) {
        String time = LocalTime.now().format(TIME_FORMAT);
        return "- [" + time + "] " + type.label + ": " + message + "\n";
    }

    private String normalizeAndValidate(String rawMessage) {
        if (rawMessage == null) {
            throw new IllegalArgumentException("message must not be null");
        }

        String normalized = rawMessage.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("message must not be blank");
        }

        return normalized;
    }

    private enum EntryType {
        CHECKPOINT("CHECKPOINT"),
        ENDDAY("ENDDAY");

        private final String label;

        EntryType(String label) {
            this.label = label;
        }
    }
}
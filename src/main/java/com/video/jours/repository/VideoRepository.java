package com.video.jours.repository;

import com.video.jours.component.ffmpeg.FFmpegManager;
import com.video.jours.component.path.PathManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.video.jours.component.path.PathType.VIDEO;

@Repository
@RequiredArgsConstructor
public class VideoRepository {
    private final PathManager pathManager;
    private final FFmpegManager ffmpegManager;

    public void processVideo(String videoId, Path originalVideoPath) throws IOException {
        Path videoPath = Files.createDirectories(pathManager.get(VIDEO, videoId));
        Process process = ffmpegManager.convert(videoPath, originalVideoPath);

        printLog(process);

        // 프로세스 완료 대기
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode);
            }
        } catch (RuntimeException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }

    private void printLog(Process process) throws IOException {
        // FFmpeg 출력 로그 확인
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(line -> System.out.println("FFmpeg: " + line));
        }
    }

}

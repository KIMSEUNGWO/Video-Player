package com.video.jours.repository;

import com.video.jours.component.path.PathManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static com.video.jours.component.path.PathType.*;

@Repository
@RequiredArgsConstructor
public class VideoRepository {

    private final PathManager pathManager;
    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    public void processVideo(String videoId, Path originalVideoPath) throws IOException {
        convertToHls(videoId, originalVideoPath);
    }

    private void convertToHls(String videoId, Path originalFilePath) throws IOException {
        Path videoPath = Files.createDirectories(pathManager.get(VIDEO, videoId));

        // FFmpeg 명령어 구성
        List<String> command = Arrays.asList(
            ffmpegPath, "-i", originalFilePath.toString(),
            "-filter_complex",
            "[0:v]split=3[v1][v2][v3];" +
                "[v1]scale=640:360[v360p];" +
                "[v2]scale=854:480[v480p];" +
                "[v3]scale=1280:720[v720p]",
            "-map", "[v360p]", "-map", "0:a",
            "-map", "[v480p]", "-map", "0:a",
            "-map", "[v720p]", "-map", "0:a",
            "-c:v", "libx264", "-c:a", "aac",
            "-b:v:0", "800k", "-b:v:1", "1200k", "-b:v:2", "2400k",
            "-var_stream_map", "v:0,a:0,name:360p v:1,a:1,name:480p v:2,a:2,name:720p",
            "-f", "hls",
            "-hls_time", "10",
            "-hls_list_size", "0",
            "-hls_segment_type", "mpegts",
            "-hls_segment_filename", videoPath + "/stream_%v/segment_%03d.ts",
            "-master_pl_name", "master.m3u8",
            videoPath + "/stream_%v/playlist.m3u8"
        );

        Process process = new ProcessBuilder(command)
            .redirectErrorStream(true) // 에러 스트림을 표준 출력으로 리다이렉트
            .start();

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

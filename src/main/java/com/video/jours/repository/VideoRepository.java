package com.video.jours.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class VideoRepository {

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Value("${video.directory}")
    private String videoDirectory;

    public void convertToHls(String inputFilePath, String videoId) throws IOException {
        String outputPath = getPathAndMkDirs(videoId);

        // FFmpeg 명령어 구성
        List<String> command = Arrays.asList(
            ffmpegPath, "-i", inputFilePath,
            "-filter_complex",
                "[0:v]split=3[v1][v2][v3];" +
                "[v1]scale=640:360[v360p];" +
                "[v2]scale=854:480[v480p];" +
                "[v3]scale=1280:720[v720p]",
            "-map", "[v360p]", "-map", "0:a",
            "-map", "[v480p]", "-map", "0:a",
            "-map", "[v720p]", "-map", "0:a",
            "-c:v", "libx264", "-c:a", "aac",
            "-b:v:0", "800k",
            "-b:v:1", "1200k",
            "-b:v:2", "2400k",
            "-var_stream_map", "v:0,a:0,name:360p v:1,a:1,name:480p v:2,a:2,name:720p",
            "-f", "hls",
            "-hls_time", "10",
            "-hls_list_size", "0",
            "-hls_segment_type", "mpegts",
            "-hls_segment_filename", outputPath + "/stream_%v/segment_%03d.ts",
            "-master_pl_name", "master.m3u8",
            outputPath + "/stream_%v/playlist.m3u8"
        );

//        String[] a = {ffmpegPath, "-i", inputFilePath,
//            "-c:v", "libx264", "-c:a", "aac",
//            "-master_pl_name", "master.m3u8",
//            "-f", "hls",
//            "-hls_time", "10",
//            "-hls_list_size", "0",
//            "-hls_segment_filename", outputPath + "/stream_%v/segment_%03d.ts",
//            outputPath + "/stream_%v.m3u8"
//        };
//        List<String> command = new ArrayList<>(List.of(a));
//
//        int qualitySize = q360p(command) + q480p(command) + q720p(command) + q1080p(command);
//        buildStreamMap(command, qualitySize);

//        // 각 화질별 스트림 디렉토리 생성
//        for (int i = 0; i < 3; i++) {
//            File streamDir = new File(outputPath + "/stream_" + i);
//            if (!streamDir.exists()) {
//                streamDir.mkdirs();
//            }
//        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);  // 에러 스트림을 표준 출력으로 리다이렉트
        Process process = pb.start();

        printLog(process);

        // 프로세스 완료 대기
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("FFmpeg process interrupted", e);
        }

    }

    private void printLog(Process process) throws IOException {
        // FFmpeg 출력 로그 확인
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("FFmpeg: " + line);  // FFmpeg의 출력을 로그로 남김
            }
        }
    }

    public String getPathAndMkDirs(String videoId) {
        String outputPath = videoDirectory + "/" + videoId;
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        return outputPath;
    }

    private int q360p(List<String> command) {
        defaultQuality(command);
        command.add("-b:v:0"); command.add("800k");
        command.add("-s:v:0"); command.add("640x360");
        return 1;
    }

    private int q480p(List<String> command) {
        defaultQuality(command);
        command.add("-b:v:1"); command.add("1200k");
        command.add("-s:v:1"); command.add("854x480");
        return 1;
    }

    private int q720p(List<String> command) {
        defaultQuality(command);
        command.add("-b:v:2"); command.add("2400k");
        command.add("-s:v:2"); command.add("1280x720");
        return 1;
    }

    private int q1080p(List<String> command) {
        defaultQuality(command);
        command.add("-b:v:3"); command.add("5000k");
        command.add("-s:v:3"); command.add("1920x1080");
        return 1;
    }

    private void buildStreamMap(List<String> command, int size) {
        command.add("-var_stream_map");

        String[] temp = new String[size];
        for (int i = 0; i < size; i++) {
            temp[i] = String.format("v:%d,a:%d", i, i);
        }
        command.add(String.join(" ", temp));
    }

    private void defaultQuality(List<String> command) {
        command.add("-map"); command.add("0:v");
        command.add("-map"); command.add("0:a");
    }

}

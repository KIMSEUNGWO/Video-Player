package com.video.jours.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageConverter {

    @Value("${path.ffmpeg}")
    private String ffmpegPath;

    public void convertToWebP(Path beforeImage, Path afterImage, int quality) throws IOException {

        // 품질 0 - 100 으로 변환
        int validQuality = Math.min(100, Math.max(0, quality));

        List<String> command = List.of(
            ffmpegPath,
            "-i", beforeImage.toString(),
            "-quality", String.valueOf(validQuality),
            "-y",  // 기존 파일 덮어쓰기
            afterImage.toString()
        );

        Process process = new ProcessBuilder(command)
            .redirectErrorStream(true)
            .start();

        // 프로세스 완료 대기
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("FFmpeg process interrupted", e);
        }
    }

    public Path convertToWebP(MultipartFile image, Path outputPath, int quality) throws IOException {
        String originalFilename = image.getOriginalFilename();

        // 임시 파일 생성
        Path tempFile = Files.createTempFile("upload_", "_" + originalFilename);

        try {
            // MultipartFile을 임시 파일로 저장
            Files.copy(image.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // WebP로 변환
            convertToWebP(tempFile, outputPath, quality);

            return outputPath;
        } finally {
            // 임시 파일 삭제
            Files.deleteIfExists(tempFile);
        }
    }

}

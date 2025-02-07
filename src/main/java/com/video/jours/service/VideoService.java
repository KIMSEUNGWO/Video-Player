package com.video.jours.service;

import com.video.jours.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    @Value("${video.directory}")
    private String directory;

    public String uploadVideo(MultipartFile video) {
        String videoId = UUID.randomUUID().toString();

        // 원본 파일 저장
        String filePath = directory + "/" + videoId;
        String originalFilePath = filePath + "_original";
        File originalFile = new File(originalFilePath);

        try {
            video.transferTo(originalFile);
            videoRepository.convertToHls(originalFilePath, videoId);
            return videoId;
        } catch (IOException e) {
            deleteDirectory(filePath);
            throw new RuntimeException(e);
        } finally {
            // 변환 완료 후 원본 파일 삭제
            originalFile.delete();
        }
    }

    private void deleteDirectory(String directoryPath) {
        try(Stream<Path> walk = Files.walk(Paths.get(directoryPath))) {
            walk.sorted(Comparator.reverseOrder()) // 하위 파일부터 삭제하기 위해 역순 정렬
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete: " + path, e);
                    }
                });
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete directory: " + directoryPath, e);
        }
    }
//    private void deleteDirectory(String directoryPath) {
//        File directoryToDelete = new File(directoryPath);
//        if (directoryToDelete.exists()) {
//            File[] allContents = directoryToDelete.listFiles();
//            if (allContents != null) {
//                for (File file : allContents) {
//                    if (file.isDirectory()) {
//                        deleteDirectory(file.getAbsolutePath());
//                    } else {
//                        file.delete();
//                    }
//                }
//            }
//            directoryToDelete.delete();
//        }
//    }


}

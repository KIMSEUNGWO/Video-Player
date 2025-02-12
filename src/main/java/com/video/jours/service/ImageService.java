package com.video.jours.service;

import com.video.jours.component.path.PathManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.video.jours.component.path.PathType.*;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final PathManager pathManager;

    public String uploadThumbnail(MultipartFile thumbnail) throws IOException {
        String extension = getFileExtension(thumbnail.getOriginalFilename());

        Path thumbnailPath = pathManager.generateOnlyPath(() ->
            pathManager.get(THUMBNAIL, pathManager.generateNewPath(extension))
        );

        Files.copy(thumbnail.getInputStream(), thumbnailPath);
        return thumbnailPath.getFileName().toString();
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return ".jpg"; // 기본 확장자 설정
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}

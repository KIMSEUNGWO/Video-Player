package com.video.jours.service;

import com.video.jours.component.DirectoryHelper;
import com.video.jours.dto.VideoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final DirectoryHelper directoryHelper;

    public void uploadThumbnail(MultipartFile thumbnail, VideoDto videoDto) throws IOException {
        String extension = getFileExtension(thumbnail.getOriginalFilename());

        Path thumbnailPath = directoryHelper.generateOnlyPath(() ->
            directoryHelper.getThumbnailPath(videoDto.newRandomThumbnail(extension))
        );

        Files.copy(thumbnail.getInputStream(), thumbnailPath);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return ".jpg"; // 기본 확장자 설정
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}

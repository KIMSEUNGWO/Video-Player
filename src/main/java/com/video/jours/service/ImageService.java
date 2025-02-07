package com.video.jours.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${image.directory}")
    private String imageDirectory;

    public void uploadThumbnail(String videoId, MultipartFile thumbnail) {
        try {
            int index = thumbnail.getOriginalFilename().lastIndexOf(".");
            String extension = thumbnail.getOriginalFilename().substring(index);

            String originalFilePath = imageDirectory + "/" + videoId + extension;
            File originalFile = new File(originalFilePath);
            if (!originalFile.exists()) {
                originalFile.mkdirs();
            }
            thumbnail.transferTo(originalFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

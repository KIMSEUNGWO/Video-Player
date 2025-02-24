package com.video.jours.service;

import com.video.jours.dto.serializable.PathType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String uploadThumbnail(MultipartFile image) throws IOException;
    String uploadOriginalVideo(MultipartFile video) throws IOException;

    void delete(PathType pathType, String storeKey);
}

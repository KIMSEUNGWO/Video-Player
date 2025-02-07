package com.video.jours.dto;

import org.springframework.web.multipart.MultipartFile;

public record RequestVideoUpload(
    MultipartFile video,
    MultipartFile thumbnail,
    String title
) {
}

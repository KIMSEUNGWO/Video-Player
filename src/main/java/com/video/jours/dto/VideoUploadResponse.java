package com.video.jours.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VideoUploadResponse {
    private final String statusKey;
    private final String message;

    public static VideoUploadResponse success(String key, String message) {
        return new VideoUploadResponse(key, message);
    }

    public static VideoUploadResponse fail(String message) {
        return new VideoUploadResponse(null, message);
    }
}

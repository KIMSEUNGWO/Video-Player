package com.video.jours.controller;

import com.video.jours.dto.VideoUploadRequest;
import com.video.jours.dto.VideoDto;
import com.video.jours.dto.VideoUploadResponse;
import com.video.jours.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<VideoUploadResponse> upload(@ModelAttribute VideoUploadRequest videoUploadRequest) {
        String statusKey = uploadService.upload(videoUploadRequest);
        return ResponseEntity.ok(new VideoUploadResponse(statusKey, "Video upload started"));
    }
}

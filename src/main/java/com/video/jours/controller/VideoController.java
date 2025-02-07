package com.video.jours.controller;

import com.video.jours.dto.RequestVideoUpload;
import com.video.jours.dto.ResponseVideoList;
import com.video.jours.service.EntityService;
import com.video.jours.service.ImageService;
import com.video.jours.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class VideoController {

    private final VideoService videoService;
    private final ImageService imageService;
    private final EntityService entityService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@ModelAttribute RequestVideoUpload requestVideoUpload) {
        System.out.println("Start");

        String videoId = videoService.uploadVideo(requestVideoUpload.video());
        imageService.uploadThumbnail(videoId, requestVideoUpload.thumbnail());

        entityService.saveVideoEntity(videoId, requestVideoUpload.title());
        System.out.println("End");
        return ResponseEntity.ok("success");
    }

    @GetMapping("/videos")
    public ResponseEntity<List<ResponseVideoList>> getVideos() {
        List<ResponseVideoList> videos = entityService.findAll();
        return ResponseEntity.ok(videos);
    }
}

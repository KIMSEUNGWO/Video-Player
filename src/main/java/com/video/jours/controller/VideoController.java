package com.video.jours.controller;

import com.video.jours.dto.ResponseVideo;
import com.video.jours.dto.ResponseVideoStatus;
import com.video.jours.service.EntityService;
import com.video.jours.service.StatusCache;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class VideoController {

    private final EntityService entityService;
    private final StatusCache statusCache;


    @GetMapping("/videos/status")
    public ResponseEntity<ResponseVideoStatus> getVideoStatus(@RequestParam(name = "q") String videoId) {
        return statusCache.get(videoId)
            .map(videoStatus -> ResponseEntity.ok(new ResponseVideoStatus(videoStatus.getStatus().name())))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/videos")
    public ResponseEntity<List<ResponseVideo>> getVideos() {
        List<ResponseVideo> videos = entityService.findAll();
        return ResponseEntity.ok(videos);
    }
}

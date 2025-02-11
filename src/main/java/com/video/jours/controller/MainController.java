package com.video.jours.controller;

import com.video.jours.dto.ResponseVideo;
import com.video.jours.service.EntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final EntityService entityService;


    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @GetMapping
    public String mainPage() {
        return "main";
    }

    @GetMapping("/status")
    public String statusPage() {
        return "status";
    }

    @GetMapping("/video/{videoId}")
    public String videoPage(@PathVariable String videoId, Model model) {
        ResponseVideo videoDto = entityService.findByVideoId(videoId);
        model.addAttribute("video", videoDto);
        return "video";
    }
}

package com.video.jours.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class MainController {


    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @GetMapping
    public String mainPage() {
        return "main";
    }

    @GetMapping("/video/{videoId}")
    public String videoPage(@PathVariable String videoId, Model model) {
        model.addAttribute("videoId", videoId);
        return "video";
    }
}

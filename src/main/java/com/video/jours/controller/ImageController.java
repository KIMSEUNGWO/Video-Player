package com.video.jours.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
public class ImageController {

    @Value("${image.directory}")
    private String imageDirectory;

    @Value("${video.directory}")
    private String videoDirectory;

    @GetMapping("/thumbnail/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        String filePath = imageDirectory + "/" + filename;
        return new UrlResource("file:" + filePath);
    }

    @GetMapping("/videos/{videoId}/{fileName}.m3u8")
    public Resource downloadVideo(@PathVariable String videoId, @PathVariable String fileName) throws MalformedURLException {
        String videoPath = videoDirectory + "/" + videoId + "/" + fileName + ".m3u8";
        return new UrlResource("file:" + videoPath);
    }

    @GetMapping("/videos/{videoId}/{stream}/{segment}")
    public Resource downloadPlaylist(@PathVariable String videoId, @PathVariable String stream, @PathVariable String segment) throws MalformedURLException {
        String videoPath = videoDirectory + "/" + videoId + "/" + stream + "/" + segment;
        return new UrlResource("file:" + videoPath);
    }

}

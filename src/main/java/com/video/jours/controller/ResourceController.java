package com.video.jours.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
public class ResourceController implements DefaultResourceMethod {

    @Value("${storage.server.address}")
    private String storageAddress;

    @GetMapping("/key.bin")
    public Resource downloadKey() throws MalformedURLException {
        return new UrlResource(concat("key.bin"));
    }

    @GetMapping("/thumbnail/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource(concat("thumbnail", filename));
    }

    @GetMapping("/play/{videoId}/master.m3u8")
    public Resource downloadVideo(@PathVariable String videoId) throws MalformedURLException {
        return new UrlResource(concat("video", videoId, "master.m3u8"));
    }

    @GetMapping("/play/{videoId}/{stream}/{segment}")
    public Resource downloadPlaylist(@PathVariable String videoId, @PathVariable String stream, @PathVariable String segment) throws MalformedURLException {
        return new UrlResource(concat("video", videoId, stream, segment));
    }

    private String concat(String... urls) {
        return storageAddress + "/" + String.join("/", urls);
    }

}

package com.video.jours.controller;

import com.video.jours.component.DirectoryHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
public class ResourceController implements DefaultResourceMethod {

    private final DirectoryHelper directoryHelper;

    @GetMapping("/thumbnail/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        Path thumbnailPath = directoryHelper.getThumbnailPath(filename);
        return resource(thumbnailPath);
    }

    @GetMapping("/videos/{videoId}/{master}.m3u8")
    public Resource downloadVideo(@PathVariable String videoId, @PathVariable String master) throws MalformedURLException {
        String playlistPath = directoryHelper.getVideoPath(videoId) + "/" + master + ".m3u8";
        return resource(playlistPath);
    }

    @GetMapping("/videos/{videoId}/{stream}/{segment}")
    public Resource downloadPlaylist(@PathVariable String videoId, @PathVariable String stream, @PathVariable String segment) throws MalformedURLException {
        String segmentPath = directoryHelper.getVideoPath(videoId) + "/" + stream + "/" + segment;
        return resource(segmentPath);
    }

}

package com.video.jours.controller;

import com.video.jours.component.path.PathManager;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;

import static com.video.jours.component.path.PathType.*;

@RestController
@RequiredArgsConstructor
public class ResourceController implements DefaultResourceMethod {

    private final PathManager pathManager;

    @GetMapping("/thumbnail/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        Path thumbnailPath = pathManager.get(THUMBNAIL, filename);
        return resource(thumbnailPath);
    }

    @GetMapping("/videos/{videoId}/{master}.m3u8")
    public Resource downloadVideo(@PathVariable String videoId, @PathVariable String master) throws MalformedURLException {
        String playlistPath = pathManager.get(VIDEO, videoId) + "/" + master + ".m3u8";
        return resource(playlistPath);
    }

    @GetMapping("/videos/{videoId}/{stream}/{segment}")
    public Resource downloadPlaylist(@PathVariable String videoId, @PathVariable String stream, @PathVariable String segment) throws MalformedURLException {
        String segmentPath = pathManager.get(VIDEO, videoId) + "/" + stream + "/" + segment;
        return resource(segmentPath);
    }

}

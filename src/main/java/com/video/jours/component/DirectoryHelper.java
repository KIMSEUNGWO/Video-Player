package com.video.jours.component;

import com.video.jours.dto.VideoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectoryHelper {

    @Value("${directory.video}")
    private String videoDirectory;
    @Value("${directory.image}")
    private String imageDirectory;

    private final SimpleFileVisitor<Path> visitor;

    public Path getVideoPath(String videoId) {
        return Paths.get(videoDirectory + "/" + videoId);
    }
    public Path getOriginalVideoPath(String videoId) {
        return Paths.get(getVideoPath(videoId) + "_original");
    }
    public Path getThumbnailPath(String thumbnail) {
        return Paths.get(imageDirectory + "/" + thumbnail);
    }


    public Path generateOnlyPath(Supplier<Path> pathSupplier) {
        Path newPath = null;
        do {
            newPath = pathSupplier.get();
        } while (Files.exists(newPath));
        return newPath;
    }

    public Path getPathAndMkDirs(Path outputPath) {
        try {
            Files.createDirectories(outputPath);
            return outputPath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories: " + outputPath, e);
        }
    }

    public void deleteAll(VideoDto videoDto) {
        if (videoDto == null) return;
        if (videoDto.getVideoId() != null) {
            deleteFromVideoId(videoDto.getVideoId());
            deleteFromOriginalVideo(videoDto.getVideoId());
        }
        if (videoDto.getThumbnail() != null) {
            deleteFromThumbnail(videoDto.getThumbnail());
        }
    }

    public void deleteFromVideoId(String videoId) {
        deleteIfExists(getVideoPath(videoId));
    }
    public void deleteFromOriginalVideo(String videoId) {
        deleteIfExists(getOriginalVideoPath(videoId));
    }
    public void deleteFromThumbnail(String thumbnail) {
        deleteIfExists(getThumbnailPath(thumbnail));
    }

    private void deleteIfExists(Path path) {
        try {
            if (Files.isDirectory(path)) {
                deleteDirectory(path);
            } else {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            log.error("Failed to delete: {}", path, e);
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        Files.walkFileTree(path, visitor);
    }

}

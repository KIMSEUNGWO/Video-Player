package com.video.jours.component.path;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbstractPathManager implements PathManager {

    @Value("${directory.video}")
    private String videoDirectory;
    @Value("${directory.original}")
    private String originalVideoDirectory;
    @Value("${directory.image}")
    private String imageDirectory;

    @Override
    public Path get(PathType pathType, String id) {
        return switch (pathType) {
            case VIDEO -> Paths.get(videoDirectory + "/" + id);
            case ORIGINAL_VIDEO -> Paths.get(originalVideoDirectory + "/" + id);
            case THUMBNAIL -> Paths.get(imageDirectory + "/" + id);
        };
    }

    @Override
    public Path generateOnlyPath(Supplier<Path> randomGeneratePath) {
        Path newPath = null;
        do {
            newPath = randomGeneratePath.get();
        } while (Files.exists(newPath));
        return newPath;
    }

    @Override
    public String generateNewPath() {
        return UUID.randomUUID().toString();
    }


}

package com.video.jours.component;

import com.video.jours.component.path.PathManager;
import com.video.jours.component.path.PathType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectoryManager {

    private final PathManager pathManager;
    private final SimpleFileVisitor<Path> deleteVisitor;

    public void delete(PathType pathType, String id) {
        if (id != null) {
            deleteIfExists(pathManager.get(pathType, id));
        }
    }

    private void deleteIfExists(Path path) {
        try {
            if (Files.isDirectory(path)) {
                Files.walkFileTree(path, deleteVisitor);
            } else {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            log.error("Failed to delete: {}", path, e);
        }
    }

}

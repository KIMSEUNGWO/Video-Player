package com.video.jours.component.path;

import java.nio.file.Path;
import java.util.function.Supplier;

public interface PathManager {

    Path generateOnlyPath(Supplier<Path> pathSupplier);

    String generateNewPath();

    default String generateNewPath(String extension) {
        return generateNewPath() + extension;
    }

    Path get(PathType pathType, String id);

}

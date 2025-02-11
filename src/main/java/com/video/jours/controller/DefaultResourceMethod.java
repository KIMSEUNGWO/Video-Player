package com.video.jours.controller;

import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;

public interface DefaultResourceMethod {

    default UrlResource resource(Path path) throws MalformedURLException {
        return new UrlResource(path.toUri());
    }
    default UrlResource resource(String path) throws MalformedURLException {
        return new UrlResource("file:".concat(path));
    }
}

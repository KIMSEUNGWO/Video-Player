package com.video.jours.controller;

import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;

public interface DefaultResourceMethod {

    default UrlResource resource(String url) throws MalformedURLException {
        return new UrlResource(url);
    }

}

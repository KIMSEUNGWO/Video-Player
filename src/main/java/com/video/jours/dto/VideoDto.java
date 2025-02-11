package com.video.jours.dto;


import lombok.Getter;

import java.util.UUID;

@Getter
public class VideoDto {

    private String videoId;
    private String thumbnail;

    public String newRandomVideoId() {
        videoId = random();
        return videoId;
    }
    public String newRandomThumbnail(String extension) {
        thumbnail = random() + extension;
        return thumbnail;
    }

    public String random() {
        return UUID.randomUUID().toString();
    }
}

package com.video.jours.service;

import com.video.jours.entity.VideoStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatusCache {

    private final Map<String, VideoStatus> map = new ConcurrentHashMap<>();

    public Optional<VideoStatus> get(String videoId) {
        return Optional.ofNullable(map.get(videoId));
    }

    public void save(String statusKey) {
        map.put(statusKey, new VideoStatus(statusKey));
    }
}

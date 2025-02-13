package com.video.jours.service;

import com.video.jours.dto.ResponseVideo;
import com.video.jours.entity.Video;
import com.video.jours.entity.VideoStatus;
import com.video.jours.repository.VideoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityService {

    private final VideoJpaRepository videoJpaRepository;

    @Transactional
    public void saveVideoEntity(String videoId, VideoStatus status) {
        Video saveVideo = Video.builder()
            .title(status.getTitle())
            .videoId(videoId)
            .thumbnail(status.getThumbnail())
            .build();
        videoJpaRepository.save(saveVideo);
    }

    public List<ResponseVideo> findAll() {
        return videoJpaRepository.findAll()
            .stream().map(video -> ResponseVideo.builder()
                .title(video.getTitle())
                .videoId(video.getVideoId())
                .thumbnail(video.getThumbnail())
                .build())
            .toList();
    }

    public ResponseVideo findByVideoId(String videoId) {
        return videoJpaRepository.findByVideoId(videoId)
            .map(video -> ResponseVideo.builder()
                .title(video.getTitle())
                .videoId(video.getVideoId())
                .thumbnail(video.getThumbnail())
                .build())
            .orElse(null);
    }
}

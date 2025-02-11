package com.video.jours.service;

import com.video.jours.dto.VideoUploadRequest;
import com.video.jours.dto.ResponseVideo;
import com.video.jours.dto.VideoDto;
import com.video.jours.entity.Video;
import com.video.jours.repository.VideoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityService {

    private final VideoJpaRepository videoJpaRepository;

    public void saveVideoEntity(VideoDto videoDto, VideoUploadRequest videoUploadRequest) {
        Video saveVideo = Video.builder()
            .title(videoUploadRequest.title())
            .videoId(videoDto.getVideoId())
            .thumbnail(videoDto.getThumbnail())
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
        return videoJpaRepository.findById(videoId)
            .map(video -> ResponseVideo.builder()
                .title(video.getTitle())
                .videoId(video.getVideoId())
                .thumbnail(video.getThumbnail())
                .build())
            .orElse(null);
    }
}

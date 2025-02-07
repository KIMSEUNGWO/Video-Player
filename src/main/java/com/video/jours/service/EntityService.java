package com.video.jours.service;

import com.video.jours.dto.ResponseVideoList;
import com.video.jours.entity.Video;
import com.video.jours.repository.VideoJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EntityService {

    private final VideoJpaRepository videoJpaRepository;

    public void saveVideoEntity(String videoId, String title) {
        Video saveVideo = Video.builder()
            .title(title)
            .videoId(videoId)
            .build();
        videoJpaRepository.save(saveVideo);
    }

    public List<ResponseVideoList> findAll() {
        return videoJpaRepository.findAll()
            .stream().map(video -> ResponseVideoList.builder()
                .title(video.getTitle())
                .videoId(video.getVideoId())
                .build())
            .toList();
    }
}

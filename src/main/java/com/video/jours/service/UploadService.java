package com.video.jours.service;

import com.video.jours.component.DirectoryHelper;
import com.video.jours.dto.VideoUploadRequest;
import com.video.jours.dto.VideoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Transactional
public class UploadService {

    private final ExecutorService executor;
    private final StatusCache statusCache;

    private final VideoService videoService;
    private final ImageService imageService;
    private final EntityService entityService;
    private final DirectoryHelper directoryHelper;

    public String upload(VideoUploadRequest upload) {

        String statusKey = UUID.randomUUID().toString();
        statusCache.save(statusKey);

        executor.submit(() -> {
            VideoDto videoDto = new VideoDto();
            try {
                imageService.uploadThumbnail(upload.thumbnail(), videoDto);
                videoService.uploadVideo(upload.video(), videoDto, statusKey);
                entityService.saveVideoEntity(videoDto, upload);
            } catch (IOException e) {
                directoryHelper.deleteAll(videoDto);
            }
        });

        return statusKey;
    }


}

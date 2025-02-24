package com.video.jours.service;

import com.video.jours.dto.VideoUploadRequest;
import com.video.jours.dto.serializable.ConvertRequest;
import com.video.jours.message.UploadMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

import static com.video.jours.dto.serializable.PathType.*;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final UploadMessageProducer producer;
    private final StorageService storageService;

    public ConvertRequest createVideoConvertRequest(VideoUploadRequest upload) {
        String statusKey = UUID.randomUUID().toString();

        String thumbnail = null;
        String originalVideo = null;

        try {
            thumbnail = storageService.uploadThumbnail(upload.thumbnail());
            originalVideo = storageService.uploadOriginalVideo(upload.video());

            return ConvertRequest.builder()
                .key(statusKey)
                .thumbnail(thumbnail)
                .originalVideo(originalVideo)
                .title(upload.title())
                .build();
        } catch (IOException e) {
            storageService.delete(THUMBNAIL, thumbnail);
            storageService.delete(ORIGINAL_VIDEO, originalVideo);
            throw new RuntimeException(e);
        }
    }


    public void upload(ConvertRequest request) {
        producer.sendMessage(request);
    }

}

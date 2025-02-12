package com.video.jours.service;

import com.video.jours.component.DirectoryManager;
import com.video.jours.component.ThreadTransactionManager;
import com.video.jours.component.path.PathManager;
import com.video.jours.dto.VideoUploadRequest;
import com.video.jours.entity.VideoStatus;
import com.video.jours.enums.ProcessingStatus;
import com.video.jours.repository.StatusJpaRepository;
import com.video.jours.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import static com.video.jours.component.path.PathType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final ExecutorService executor;
    private final StatusRepository statusRepository;

    private final PathManager pathManager;
    private final DirectoryManager directoryManager;
    private final ThreadTransactionManager threadTransactionManager;

    private final VideoService videoService;
    private final ImageService imageService;
    private final EntityService entityService;
    private final StatusJpaRepository statusJpaRepository;

    @Transactional
    public String uploadSchedule(VideoUploadRequest upload) {
        String statusKey = UUID.randomUUID().toString();

        String thumbnail = null;
        String originalVideo = null;

        try {
            thumbnail = imageService.uploadThumbnail(upload.thumbnail());
            originalVideo = videoService.uploadOriginalVideo(upload.video());

            statusRepository.save(VideoStatus.builder()
                .statusKey(statusKey)
                .status(ProcessingStatus.PENDING)
                .thumbnail(thumbnail)
                .originalVideo(originalVideo)
                .title(upload.title())
                .build());
        } catch (IOException e) {
            directoryManager.delete(THUMBNAIL, thumbnail);
            directoryManager.delete(ORIGINAL_VIDEO, originalVideo);
            throw new RuntimeException(e);
        }

        return statusKey;
    }


    public void upload(String statusKey) {
        try {
            executor.submit(submit(statusKey));
        } catch (RejectedExecutionException e) { // Thread Exception
            log.error("Upload rejected: {}", e.getMessage(), e);
            statusRepository.findById(statusKey).ifPresent(status -> status.setError("Upload rejected due to server load"));
            throw new RuntimeException("Server is busy, please try again later", e);
        }
    }

    private Runnable submit(String statusKey) {
        return () -> {
            Optional<VideoStatus> findStatus = statusJpaRepository.findById(statusKey);

            String videoId = pathManager.generateOnlyPath(() ->
                pathManager.get(VIDEO, pathManager.generateNewPath())
            ).getFileName().toString();

            VideoStatus status = null;
            try {
                status = findStatus
                    .orElseThrow(() -> new RuntimeException("statusKey not found"));

                videoService.generateHls(videoId, status);
                entityService.saveVideoEntity(videoId, status);
            } catch (Exception e) {
                log.error("Error processing upload: {}", e.getMessage(), e);
                findStatus.ifPresent(videoStatus -> {
                    directoryManager.delete(THUMBNAIL, videoStatus.getThumbnail());
                    directoryManager.delete(ORIGINAL_VIDEO, videoStatus.getOriginalVideo());
                    directoryManager.delete(VIDEO, videoId);
                    threadTransactionManager.updateStatus(videoStatus, data -> data.setError("Upload failed: " + e.getMessage()));
                });
            } finally {
                findStatus.ifPresent(videoStatus -> {
                        directoryManager.delete(ORIGINAL_VIDEO, videoStatus.getOriginalVideo());
                });
            }
        };
    }
}

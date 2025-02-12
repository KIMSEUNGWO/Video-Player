package com.video.jours.service;

import com.video.jours.component.path.PathManager;
import com.video.jours.component.ThreadTransactionManager;
import com.video.jours.entity.VideoStatus;
import com.video.jours.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

import static com.video.jours.component.path.PathType.*;
import static com.video.jours.enums.ProcessingStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final PathManager pathManager;
    private final VideoRepository videoRepository;

    private final ThreadTransactionManager threadTransactionManager;

    public String uploadOriginalVideo(MultipartFile originalVideo) throws IOException {
        Path originalVideoFilePath = pathManager.generateOnlyPath(() ->
            pathManager.get(ORIGINAL_VIDEO, pathManager.generateNewPath())
        );

        // 필요한 디렉토리 생성
        Files.createDirectories(originalVideoFilePath.getParent());

        // 파일 저장
        Files.copy(originalVideo.getInputStream(), originalVideoFilePath);
        return originalVideoFilePath.getFileName().toString();
    }

    // 원본 영상을 서버에 저장해놓고 Thread로 HLS 변환 작업하는 로직을 작성중임
    public void generateHls(String videoId, VideoStatus status) throws IOException {
        threadTransactionManager.updateStatus(status, PROCESSING);

        videoRepository.processVideo(videoId, pathManager.get(ORIGINAL_VIDEO, status.getOriginalVideo()));

        threadTransactionManager.updateStatus(status, COMPLETED);
    }


}

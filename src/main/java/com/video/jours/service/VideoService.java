package com.video.jours.service;

import com.video.jours.component.DirectoryHelper;
import com.video.jours.dto.VideoDto;
import com.video.jours.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final DirectoryHelper directoryHelper;
    private final VideoRepository videoRepository;

    public void uploadVideo(MultipartFile video, VideoDto videoDto, String statusKey) throws IOException {

        Path originalVideoFilePath = directoryHelper.generateOnlyPath(() ->
            directoryHelper.getOriginalVideoPath(videoDto.newRandomVideoId())
        );

        // 필요한 디렉토리 생성
        Files.createDirectories(originalVideoFilePath.getParent());

        // 파일 저장
        Files.copy(video.getInputStream(), originalVideoFilePath);

        // 비동기 변환 프로세스 시작
        videoRepository.processVideo(statusKey, originalVideoFilePath, videoDto);
    }


}

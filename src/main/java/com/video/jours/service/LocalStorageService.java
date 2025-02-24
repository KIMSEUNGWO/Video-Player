package com.video.jours.service;

import com.video.jours.dto.serializable.PathType;
import com.video.jours.dto.serializable.StorageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class LocalStorageService implements StorageService {

    private final RestClient restClient;

    public LocalStorageService(@Value("${storage.server.address}") String address) {
        this.restClient = RestClient.create(address);
    }

    @Override
    public String uploadThumbnail(MultipartFile image) throws IOException {
        return uploadFile("/api/v1/upload/thumbnail", image);
    }

    @Override
    public String uploadOriginalVideo(MultipartFile video) throws IOException {
        return uploadFile("/api/v1/upload/original", video);
    }

    @Override
    public void delete(PathType pathType, String storeKey) {
        if (pathType == null || storeKey == null || storeKey.isEmpty()) return;

        restClient.delete()
            .uri(builder -> builder
                .path("/api/v1/delete")
                .queryParam("pathType", pathType)
                .queryParam("storeKey", storeKey)
                .build()
            )
            .retrieve()
            .onStatus(response -> false)
            .toEntity(String.class);

    }

    private String uploadFile(String path, MultipartFile file) throws IOException {
        try {
            // RestClient로 전송
            StorageResponse response = restClient.post()
                .uri(path)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fileWrapper(file))
                .retrieve()
                .onStatus(code -> code.is4xxClientError() || code.is5xxServerError(), (request, response1) -> {
                    throw new IOException("Upload failed");
                })
                .toEntity(StorageResponse.class)  // 응답을 StorageResponse 로 변환
                .getBody();             // ResponseEntity 에서 body 추출

            if (response == null) {
                throw new IOException("Storage server returned null");
            }

            return response.getStoreKey();

        } catch (RestClientException e) {
            throw new IOException("Upload failed", e);
        }
    }

    private MultiValueMap<String, Object> fileWrapper(MultipartFile file) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        return body;
    }
}

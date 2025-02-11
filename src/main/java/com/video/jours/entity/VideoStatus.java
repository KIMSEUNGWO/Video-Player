package com.video.jours.entity;

import lombok.Getter;

@Getter
public class VideoStatus {

    private final String statusKey;
    private ProcessingStatus status;
    private String errorMessage;

    public enum ProcessingStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }

    public VideoStatus(String statusKey) {
        this.statusKey = statusKey;
        this.status = ProcessingStatus.PENDING;
    }

    public void updateStatus(ProcessingStatus status) {
        this.status = status;
    }

    public void setError(String errorMessage) {
        this.status = ProcessingStatus.FAILED;
        this.errorMessage = errorMessage;
    }
}

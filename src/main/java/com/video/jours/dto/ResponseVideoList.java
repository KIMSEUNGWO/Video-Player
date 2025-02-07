package com.video.jours.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ResponseVideoList {

    private final String videoId;
    private final String title;
}

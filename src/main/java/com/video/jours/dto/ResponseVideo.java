package com.video.jours.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ResponseVideo {

    private final String videoId;
    private final String thumbnail;
    private final String title;
}

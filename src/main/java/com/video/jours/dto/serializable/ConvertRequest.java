package com.video.jours.dto.serializable;

import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConvertRequest {

    private String key;
    private String thumbnail;
    private String originalVideo;

    private String title;
}

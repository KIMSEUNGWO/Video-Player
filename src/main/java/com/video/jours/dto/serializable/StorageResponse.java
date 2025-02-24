package com.video.jours.dto.serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StorageResponse {

    private String storeKey;
    private String message;
}

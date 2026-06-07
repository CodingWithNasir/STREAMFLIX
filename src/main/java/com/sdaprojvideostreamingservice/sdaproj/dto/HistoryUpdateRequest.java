package com.sdaprojvideostreamingservice.sdaproj.dto;

import lombok.Data;

@Data
public class HistoryUpdateRequest {
    private Integer progressSeconds;
    private Boolean completed;
}

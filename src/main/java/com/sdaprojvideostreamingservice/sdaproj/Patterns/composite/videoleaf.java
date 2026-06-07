package com.sdaprojvideostreamingservice.sdaproj.Patterns.composite;

import com.sdaprojvideostreamingservice.sdaproj.dto.CategoryNodeDto;
import com.sdaprojvideostreamingservice.sdaproj.dto.VideoDto;

public class videoleaf implements contentcomponent {

    private final VideoDto video;

    public videoleaf(VideoDto video) {
        this.video = video;
    }

    @Override
    public CategoryNodeDto toNode() {
        return CategoryNodeDto.builder()
                .name(video.getTitle())
                .type("video")
                .video(video)
                .build();
    }
}

package com.sdaprojvideostreamingservice.sdaproj.Patterns.bridge;

import com.sdaprojvideostreamingservice.sdaproj.dto.StreamResponse;
import com.sdaprojvideostreamingservice.sdaproj.model.StreamQuality;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class streamingservice {

    private final Map<StreamQuality, Videoquality> qualities;

    public streamingservice(SDqaulity sd, HDQuality hd, FullHDQuality fullHd, UltraHdQuality ultraHd) {
        this.qualities = Map.of(
                StreamQuality.SD, sd,
                StreamQuality.HD, hd,
                StreamQuality.FULL_HD, fullHd,
                StreamQuality.ULTRA_HD, ultraHd);
    }

    public StreamResponse buildStream(Long videoId, String baseUrl, StreamQuality quality) {
        Videoquality bridge = qualities.getOrDefault(quality, qualities.get(StreamQuality.HD));
        return StreamResponse.builder()
                .videoId(videoId)
                .streamUrl(bridge.applyToUrl(baseUrl))
                .quality(quality)
                .qualityLabel(bridge.getLabel())
                .build();
    }
}

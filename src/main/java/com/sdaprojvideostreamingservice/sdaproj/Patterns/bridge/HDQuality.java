package com.sdaprojvideostreamingservice.sdaproj.Patterns.bridge;

import org.springframework.stereotype.Component;

@Component
public class HDQuality implements Videoquality {
    @Override
    public String getLabel() { return "HD"; }

    @Override
    public String applyToUrl(String baseUrl) {
        return baseUrl + "?quality=hd";
    }
}

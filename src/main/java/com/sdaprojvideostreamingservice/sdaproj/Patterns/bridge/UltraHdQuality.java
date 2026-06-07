package com.sdaprojvideostreamingservice.sdaproj.Patterns.bridge;

import org.springframework.stereotype.Component;

@Component
public class UltraHdQuality implements Videoquality {
    @Override
    public String getLabel() { return "Ultra HD"; }

    @Override
    public String applyToUrl(String baseUrl) {
        return baseUrl + "?quality=ultrahd";
    }
}

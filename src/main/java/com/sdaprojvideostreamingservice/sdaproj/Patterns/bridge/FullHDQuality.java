package com.sdaprojvideostreamingservice.sdaproj.Patterns.bridge;

import org.springframework.stereotype.Component;

@Component
public class FullHDQuality implements Videoquality {
    @Override
    public String getLabel() { return "Full HD"; }

    @Override
    public String applyToUrl(String baseUrl) {
        return baseUrl + "?quality=fullhd";
    }
}

package com.sdaprojvideostreamingservice.sdaproj.Patterns.bridge;

import org.springframework.stereotype.Component;

@Component
public class SDqaulity implements Videoquality {
    @Override
    public String getLabel() { return "SD"; }

    @Override
    public String applyToUrl(String baseUrl) {
        return baseUrl + "?quality=sd";
    }
}

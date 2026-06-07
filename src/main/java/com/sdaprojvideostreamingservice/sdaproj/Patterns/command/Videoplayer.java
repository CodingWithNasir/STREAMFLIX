package com.sdaprojvideostreamingservice.sdaproj.Patterns.command;

import org.springframework.stereotype.Component;

@Component
public class Videoplayer {
    private boolean playing;

    public void play() { playing = true; }
    public void pause() { playing = false; }
    public boolean isPlaying() { return playing; }
}

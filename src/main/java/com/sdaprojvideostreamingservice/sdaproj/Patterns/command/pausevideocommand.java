package com.sdaprojvideostreamingservice.sdaproj.Patterns.command;

import org.springframework.stereotype.Component;

@Component
public class pausevideocommand implements command {

    private final Videoplayer player;

    public pausevideocommand(Videoplayer player) {
        this.player = player;
    }

    @Override
    public void execute() { player.pause(); }

    @Override
    public String getName() { return "pause"; }
}

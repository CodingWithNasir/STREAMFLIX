package com.sdaprojvideostreamingservice.sdaproj.Patterns.command;

import org.springframework.stereotype.Component;

@Component
public class playvideocommand implements command {

    private final Videoplayer player;

    public playvideocommand(Videoplayer player) {
        this.player = player;
    }

    @Override
    public void execute() { player.play(); }

    @Override
    public String getName() { return "play"; }
}

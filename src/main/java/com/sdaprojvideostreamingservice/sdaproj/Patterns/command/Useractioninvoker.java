package com.sdaprojvideostreamingservice.sdaproj.Patterns.command;

import org.springframework.stereotype.Component;

@Component
public class Useractioninvoker {

    public void invoke(command cmd) {
        cmd.execute();
    }
}

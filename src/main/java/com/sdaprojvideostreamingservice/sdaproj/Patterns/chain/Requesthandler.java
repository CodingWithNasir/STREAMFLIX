package com.sdaprojvideostreamingservice.sdaproj.Patterns.chain;

import com.sdaprojvideostreamingservice.sdaproj.dto.VideoDto;
import com.sdaprojvideostreamingservice.sdaproj.model.User;
import com.sdaprojvideostreamingservice.sdaproj.model.videoentity;

public abstract class Requesthandler {
    private Requesthandler next;

    public Requesthandler linkWith(Requesthandler next) {
        this.next = next;
        return next;
    }

    public void check(User user, videoentity video) {
        doCheck(user, video);
        if (next != null) {
            next.check(user, video);
        }
    }

    protected abstract void doCheck(User user, videoentity video);

    public abstract String getFailureReason();
}

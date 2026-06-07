package com.sdaprojvideostreamingservice.sdaproj.Patterns.chain;

import com.sdaprojvideostreamingservice.sdaproj.model.User;
import com.sdaprojvideostreamingservice.sdaproj.model.videoentity;
import org.springframework.stereotype.Component;

@Component
public class StreamingAccessChain {

    private final Requesthandler chain;

    public StreamingAccessChain(SubscriptionValidationHandler subscriptionHandler,
                                AgeRestrictionHandler ageHandler,
                                StreamingAccessHandler streamingHandler) {
        subscriptionHandler.linkWith(ageHandler).linkWith(streamingHandler);
        this.chain = subscriptionHandler;
    }

    public void validateAccess(User user, videoentity video) {
        chain.check(user, video);
    }
}

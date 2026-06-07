package com.sdaprojvideostreamingservice.sdaproj.Patterns.chain;

import com.sdaprojvideostreamingservice.sdaproj.exception.ApiException;
import com.sdaprojvideostreamingservice.sdaproj.model.User;
import com.sdaprojvideostreamingservice.sdaproj.model.subscription;
import com.sdaprojvideostreamingservice.sdaproj.model.videoentity;
import com.sdaprojvideostreamingservice.sdaproj.repository.SubscriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class StreamingAccessHandler extends Requesthandler {

    private final SubscriptionRepository subscriptionRepository;
    private final AgeRestrictionHandler ageRestrictionHandler;

    public StreamingAccessHandler(SubscriptionRepository subscriptionRepository,
                                  AgeRestrictionHandler ageRestrictionHandler) {
        this.subscriptionRepository = subscriptionRepository;
        this.ageRestrictionHandler = ageRestrictionHandler;
    }

    @Override
    protected void doCheck(User user, videoentity video) {
        subscription sub = subscriptionRepository.findByUserId(user.getUserId()).orElse(null);
        String plan = sub != null && sub.getStatus().name().equals("ACTIVE")
                && !sub.getEndDate().isBefore(LocalDate.now())
                ? sub.getPlan().name() : "NONE";
        ageRestrictionHandler.enforceForPlan(plan, video.getAgeRating());
        if (video.getVideoUrl() == null || video.getVideoUrl().isBlank()) {
            throw new ApiException(HttpStatus.NOT_FOUND, getFailureReason());
        }
    }

    @Override
    public String getFailureReason() {
        return "Streaming access denied";
    }
}

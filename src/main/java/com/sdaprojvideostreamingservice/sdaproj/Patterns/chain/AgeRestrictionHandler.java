package com.sdaprojvideostreamingservice.sdaproj.Patterns.chain;

import com.sdaprojvideostreamingservice.sdaproj.exception.ApiException;
import com.sdaprojvideostreamingservice.sdaproj.model.AgeRating;
import com.sdaprojvideostreamingservice.sdaproj.model.User;
import com.sdaprojvideostreamingservice.sdaproj.model.videoentity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AgeRestrictionHandler extends Requesthandler {

    @Override
    protected void doCheck(User user, videoentity video) {
        if (video.getAgeRating() == AgeRating.R || video.getAgeRating() == AgeRating.NC17) {
            if (user.getRole().name().equals("USER")) {
                // Users on BASIC plan blocked from R/NC17 — enforced via subscription plan in streaming handler
            }
        }
    }

    @Override
    public String getFailureReason() {
        return "Content restricted by age rating";
    }

    public void enforceForPlan(String plan, AgeRating rating) {
        if ("BASIC".equals(plan) && (rating == AgeRating.R || rating == AgeRating.NC17)) {
            throw new ApiException(HttpStatus.FORBIDDEN, getFailureReason());
        }
    }
}

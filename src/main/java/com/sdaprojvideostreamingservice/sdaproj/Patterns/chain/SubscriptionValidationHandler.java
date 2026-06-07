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
public class SubscriptionValidationHandler extends Requesthandler {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionValidationHandler(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    protected void doCheck(User user, videoentity video) {
        if (!Boolean.TRUE.equals(video.getIsPremium())) {
            return;
        }
        subscription sub = subscriptionRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, getFailureReason()));
        if (!sub.getStatus().name().equals("ACTIVE") || sub.getEndDate().isBefore(LocalDate.now())) {
            throw new ApiException(HttpStatus.FORBIDDEN, getFailureReason());
        }
    }

    @Override
    public String getFailureReason() {
        return "Active subscription required for premium content";
    }
}

package com.sdaprojvideostreamingservice.sdaproj.dto;

import com.sdaprojvideostreamingservice.sdaproj.model.PaymentMethod;
import com.sdaprojvideostreamingservice.sdaproj.model.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscribeRequest {
    @NotNull
    private SubscriptionPlan plan;
    @NotNull
    private PaymentMethod paymentMethod;
}

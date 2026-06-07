package com.sdaprojvideostreamingservice.sdaproj.dto;

import com.sdaprojvideostreamingservice.sdaproj.model.SubscriptionPlan;
import com.sdaprojvideostreamingservice.sdaproj.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Long subscriptionId;
    private Long userId;
    private SubscriptionPlan plan;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
}

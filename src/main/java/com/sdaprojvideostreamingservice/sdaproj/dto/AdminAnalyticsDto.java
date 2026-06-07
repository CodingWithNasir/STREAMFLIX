package com.sdaprojvideostreamingservice.sdaproj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAnalyticsDto {
    private long totalUsers;
    private long totalVideos;
    private long activeSubscribers;
    private long totalWatchlistItems;
    private BigDecimal totalRevenue;
}

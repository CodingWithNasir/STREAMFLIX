package com.sdaprojvideostreamingservice.sdaproj.dto;

import com.sdaprojvideostreamingservice.sdaproj.model.AgeRating;
import com.sdaprojvideostreamingservice.sdaproj.model.VideoGenre;
import com.sdaprojvideostreamingservice.sdaproj.model.VideoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDto {
    private Long videoId;
    private String title;
    private String description;
    private VideoGenre genre;
    private Integer durationSeconds;
    private Integer releaseYear;
    private BigDecimal rating;
    private AgeRating ageRating;
    private String thumbnailUrl;
    private String videoUrl;
    private VideoType type;
    private Boolean isPremium;
    private Boolean inWatchlist;
}

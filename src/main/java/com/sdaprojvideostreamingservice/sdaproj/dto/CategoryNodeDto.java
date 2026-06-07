package com.sdaprojvideostreamingservice.sdaproj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryNodeDto {
    private String name;
    private String type;
    private List<CategoryNodeDto> children;
    private VideoDto video;
}

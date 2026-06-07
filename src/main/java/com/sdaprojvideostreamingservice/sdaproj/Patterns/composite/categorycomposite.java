package com.sdaprojvideostreamingservice.sdaproj.Patterns.composite;

import com.sdaprojvideostreamingservice.sdaproj.dto.CategoryNodeDto;

import java.util.ArrayList;
import java.util.List;

public class categorycomposite implements contentcomponent {

    private final String name;
    private final List<contentcomponent> children = new ArrayList<>();

    public categorycomposite(String name) {
        this.name = name;
    }

    public void add(contentcomponent child) {
        children.add(child);
    }

    @Override
    public CategoryNodeDto toNode() {
        List<CategoryNodeDto> childNodes = children.stream().map(contentcomponent::toNode).toList();
        return CategoryNodeDto.builder()
                .name(name)
                .type("category")
                .children(childNodes)
                .build();
    }
}

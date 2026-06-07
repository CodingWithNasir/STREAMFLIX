package com.sdaprojvideostreamingservice.sdaproj.repository;

import com.sdaprojvideostreamingservice.sdaproj.model.VideoGenre;
import com.sdaprojvideostreamingservice.sdaproj.model.videoentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<videoentity, Long> {
    List<videoentity> findByGenre(VideoGenre genre);
}

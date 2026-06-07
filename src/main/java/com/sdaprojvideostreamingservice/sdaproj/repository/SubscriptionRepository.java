package com.sdaprojvideostreamingservice.sdaproj.repository;

import com.sdaprojvideostreamingservice.sdaproj.model.subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<subscription, Long> {
    Optional<subscription> findByUserId(Long userId);
}

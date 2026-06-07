package com.sdaprojvideostreamingservice.sdaproj.repository;

import com.sdaprojvideostreamingservice.sdaproj.model.payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<payment, Long> {
    List<payment> findByUserIdOrderByPaymentDateDesc(Long userId);
}

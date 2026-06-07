package com.sdaprojvideostreamingservice.sdaproj.service;

import com.sdaprojvideostreamingservice.sdaproj.dto.PlanDto;
import com.sdaprojvideostreamingservice.sdaproj.dto.SubscribeRequest;
import com.sdaprojvideostreamingservice.sdaproj.dto.SubscriptionDTO;
import com.sdaprojvideostreamingservice.sdaproj.exception.ApiException;
import com.sdaprojvideostreamingservice.sdaproj.model.SubscriptionPlan;
import com.sdaprojvideostreamingservice.sdaproj.model.SubscriptionStatus;
import com.sdaprojvideostreamingservice.sdaproj.model.subscription;
import com.sdaprojvideostreamingservice.sdaproj.repository.SubscriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentService paymentService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, PaymentService paymentService) {
        this.subscriptionRepository = subscriptionRepository;
        this.paymentService = paymentService;
    }

    public List<PlanDto> getPlans() {
        return List.of(
                PlanDto.builder().plan(SubscriptionPlan.BASIC).name("Basic")
                        .priceMonthly(new BigDecimal("9.99"))
                        .description("Essential streaming infrastructure access")
                        .features(List.of("SD quality", "1 device", "Standard catalog")).build(),
                PlanDto.builder().plan(SubscriptionPlan.STANDARD).name("Standard")
                        .priceMonthly(new BigDecimal("12.99"))
                        .description("Professional delivery for growing teams")
                        .features(List.of("HD quality", "2 devices", "Full catalog", "Watchlist")).build(),
                PlanDto.builder().plan(SubscriptionPlan.PREMIUM).name("Premium")
                        .priceMonthly(new BigDecimal("15.99"))
                        .description("Enterprise-grade streaming with full fidelity")
                        .features(List.of("Ultra HD", "4 devices", "Premium catalog", "Priority support")).build()
        );
    }

    public SubscriptionDTO getCurrent(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .map(this::toDto)
                .orElse(null);
    }

    public SubscriptionDTO subscribe(Long userId, SubscribeRequest request) {
        BigDecimal price = getPlans().stream()
                .filter(p -> p.getPlan() == request.getPlan())
                .findFirst()
                .map(PlanDto::getPriceMonthly)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid plan"));
        paymentService.processPayment(userId, price, request.getPaymentMethod());
        subscription sub = subscriptionRepository.findByUserId(userId).orElse(
                subscription.builder().userId(userId).build());
        sub.setPlan(request.getPlan());
        sub.setStartDate(LocalDate.now());
        sub.setEndDate(LocalDate.now().plusDays(30));
        sub.setStatus(SubscriptionStatus.ACTIVE);
        return toDto(subscriptionRepository.save(sub));
    }

    public void cancel(Long userId) {
        subscription sub = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No subscription found"));
        sub.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(sub);
    }

    public long countActive() {
        return subscriptionRepository.findAll().stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE
                        && !s.getEndDate().isBefore(LocalDate.now()))
                .count();
    }

    private SubscriptionDTO toDto(subscription s) {
        return SubscriptionDTO.builder()
                .subscriptionId(s.getSubscriptionId())
                .userId(s.getUserId())
                .plan(s.getPlan())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .status(s.getStatus())
                .build();
    }
}

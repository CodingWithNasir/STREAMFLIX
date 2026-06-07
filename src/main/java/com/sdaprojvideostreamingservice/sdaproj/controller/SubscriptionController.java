package com.sdaprojvideostreamingservice.sdaproj.controller;

import com.sdaprojvideostreamingservice.sdaproj.dto.PaymentDTO;
import com.sdaprojvideostreamingservice.sdaproj.dto.PlanDto;
import com.sdaprojvideostreamingservice.sdaproj.dto.SubscribeRequest;
import com.sdaprojvideostreamingservice.sdaproj.dto.SubscriptionDTO;
import com.sdaprojvideostreamingservice.sdaproj.security.SecurityUtils;
import com.sdaprojvideostreamingservice.sdaproj.service.PaymentService;
import com.sdaprojvideostreamingservice.sdaproj.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;

    public SubscriptionController(SubscriptionService subscriptionService, PaymentService paymentService) {
        this.subscriptionService = subscriptionService;
        this.paymentService = paymentService;
    }

    @GetMapping("/plans")
    public List<PlanDto> plans() {
        return subscriptionService.getPlans();
    }

    @GetMapping("/me")
    public SubscriptionDTO current() {
        return subscriptionService.getCurrent(SecurityUtils.currentUserId());
    }

    @PostMapping
    public SubscriptionDTO subscribe(@Valid @RequestBody SubscribeRequest request) {
        return subscriptionService.subscribe(SecurityUtils.currentUserId(), request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel() {
        subscriptionService.cancel(SecurityUtils.currentUserId());
    }

    @GetMapping("/payments")
    public List<PaymentDTO> paymentHistory() {
        return paymentService.getHistory(SecurityUtils.currentUserId());
    }
}

package com.sdaprojvideostreamingservice.sdaproj.service;

import com.sdaprojvideostreamingservice.sdaproj.Patterns.adapter.paymentgateway;
import com.sdaprojvideostreamingservice.sdaproj.Patterns.adapter.paypaladapter;
import com.sdaprojvideostreamingservice.sdaproj.Patterns.adapter.stripeadapter;
import com.sdaprojvideostreamingservice.sdaproj.dto.PaymentDTO;
import com.sdaprojvideostreamingservice.sdaproj.exception.ApiException;
import com.sdaprojvideostreamingservice.sdaproj.model.PaymentMethod;
import com.sdaprojvideostreamingservice.sdaproj.model.payment;
import com.sdaprojvideostreamingservice.sdaproj.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final Map<PaymentMethod, paymentgateway> gateways;

    public PaymentService(PaymentRepository paymentRepository, stripeadapter stripe, paypaladapter paypal) {
        this.paymentRepository = paymentRepository;
        this.gateways = Map.of(PaymentMethod.STRIPE, stripe, PaymentMethod.PAYPAL, paypal);
    }

    public PaymentDTO processPayment(Long userId, BigDecimal amount, PaymentMethod method) {
        paymentgateway gateway = gateways.get(method);
        if (gateway == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported payment method");
        }
        PaymentDTO result = gateway.charge(userId, amount, "USD");
        payment entity = payment.builder()
                .userId(result.getUserId())
                .amount(result.getAmount())
                .currency(result.getCurrency())
                .paymentMethod(result.getPaymentMethod())
                .gatewayTxnId(result.getGatewayTxnId())
                .paymentDate(result.getPaymentDate())
                .status(result.getStatus())
                .build();
        PaymentDTO saved = toDto(paymentRepository.save(entity));
        saved.setPaymentId(entity.getPaymentId());
        return saved;
    }

    public List<PaymentDTO> getHistory(Long userId) {
        return paymentRepository.findByUserIdOrderByPaymentDateDesc(userId).stream()
                .map(this::toDto).toList();
    }

    public BigDecimal totalRevenue() {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getStatus().name().equals("SUCCESS"))
                .map(payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PaymentDTO toDto(payment p) {
        return PaymentDTO.builder()
                .paymentId(p.getPaymentId())
                .userId(p.getUserId())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .paymentMethod(p.getPaymentMethod())
                .gatewayTxnId(p.getGatewayTxnId())
                .paymentDate(p.getPaymentDate())
                .status(p.getStatus())
                .build();
    }
}

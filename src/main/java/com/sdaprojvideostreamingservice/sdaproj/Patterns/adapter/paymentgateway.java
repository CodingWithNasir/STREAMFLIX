package com.sdaprojvideostreamingservice.sdaproj.Patterns.adapter;

import com.sdaprojvideostreamingservice.sdaproj.dto.PaymentDTO;

import java.math.BigDecimal;

public interface paymentgateway {
    PaymentDTO charge(Long userId, BigDecimal amount, String currency);
    String getProviderName();
}

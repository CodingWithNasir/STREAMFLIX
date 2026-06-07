package com.sdaprojvideostreamingservice.sdaproj.Patterns.adapter;

import com.sdaprojvideostreamingservice.sdaproj.dto.PaymentDTO;
import com.sdaprojvideostreamingservice.sdaproj.model.PaymentMethod;
import com.sdaprojvideostreamingservice.sdaproj.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class paypaladapter implements paymentgateway {

    @Override
    public PaymentDTO charge(Long userId, BigDecimal amount, String currency) {
        return PaymentDTO.builder()
                .userId(userId)
                .amount(amount)
                .currency(currency)
                .paymentMethod(PaymentMethod.PAYPAL)
                .gatewayTxnId("txn_paypal_" + UUID.randomUUID().toString().substring(0, 8))
                .paymentDate(Instant.now())
                .status(PaymentStatus.SUCCESS)
                .build();
    }

    @Override
    public String getProviderName() { return "PayPal"; }
}

package com.sdaprojvideostreamingservice.sdaproj.dto;

import com.sdaprojvideostreamingservice.sdaproj.model.PaymentMethod;
import com.sdaprojvideostreamingservice.sdaproj.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private String gatewayTxnId;
    private Instant paymentDate;
    private PaymentStatus status;
}

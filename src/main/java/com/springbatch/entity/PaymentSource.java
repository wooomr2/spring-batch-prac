package com.springbatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_source")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String partnerCorpName;

    @Column(nullable = false)
    private BigDecimal originalAmount;

    @Column(nullable = false)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private BigDecimal finalAmount;

    @Column(nullable = false)
    private LocalDate paymentDate;
}

package com.springbatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_source")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String partnerCorpName;

    @Comment("파트너 사업자번호")
    @Column(nullable = false, length = 100)
    private String partnerBusinessRegistrationNumber;

    @Column(nullable = false)
    private BigDecimal originalAmount;

    @Column(nullable = false)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private BigDecimal finalAmount;

    @Column(nullable = false)
    private LocalDate paymentDate;
}

package com.springbatch.repository;

import com.springbatch.entity.PaymentSource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentSourceRepository extends JpaRepository<PaymentSource, Long> {
}

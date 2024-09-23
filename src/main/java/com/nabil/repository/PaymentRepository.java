package com.nabil.repository;

import com.nabil.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentOrder, Long> {
}

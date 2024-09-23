package com.nabil.repository;

import com.nabil.model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {
    PaymentDetails findByUser(Long userId);
}

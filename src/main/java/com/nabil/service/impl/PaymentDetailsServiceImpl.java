package com.nabil.service.impl;

import com.nabil.model.PaymentDetails;
import com.nabil.model.User;
import com.nabil.repository.PaymentDetailsRepository;
import com.nabil.service.PaymentDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentDetailsServiceImpl implements PaymentDetailsService {

    private final PaymentDetailsRepository paymentDetailsRepository;

    @Override
    public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String ifsc, String bankName, User user) {
        PaymentDetails paymentDetails = new PaymentDetails();

        paymentDetails.setUser(user);
        paymentDetails.setIfsc(ifsc);
        paymentDetails.setBankName(bankName);
        paymentDetails.setAccountNumber(accountNumber);
        paymentDetails.setAccountHolderName(accountHolderName);

        return paymentDetailsRepository.save(paymentDetails);
    }

    @Override
    public PaymentDetails getusersPaymentDetails(User user) {
        return paymentDetailsRepository.findByUserId(user.getId());
    }
}

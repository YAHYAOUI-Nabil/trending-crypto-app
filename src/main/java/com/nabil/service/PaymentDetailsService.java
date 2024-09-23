package com.nabil.service;

import com.nabil.model.PaymentDetails;
import com.nabil.model.User;

public interface PaymentDetailsService {
    PaymentDetails addPaymentDetails(String accountNumber,
                                     String accountHolderName,
                                     String ifsc,
                                     String bankName,
                                     User user);

    PaymentDetails getusersPaymentDetails(User user);
}

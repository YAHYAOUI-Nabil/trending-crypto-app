package com.nabil.service;

import com.nabil.domain.PaymentMethod;
import com.nabil.model.PaymentOrder;
import com.nabil.model.User;
import com.nabil.response.PaymentResponse;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

public interface PaymentService {

    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id) throws Exception;

    Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String PaymentId) throws RazorpayException;

    PaymentResponse createRazorpayPaymentLing(User user, Long amount, Long orderId);

    PaymentResponse createStripePaymentLing(User user, Long amount, Long orderId) throws StripeException;

}

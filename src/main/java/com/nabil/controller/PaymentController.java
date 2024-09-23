package com.nabil.controller;

import com.nabil.domain.PaymentMethod;
import com.nabil.model.PaymentOrder;
import com.nabil.model.User;
import com.nabil.response.PaymentResponse;
import com.nabil.service.PaymentService;
import com.nabil.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private final UserService userService;

    @PostMapping("/{paymentMethod}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @RequestHeader("Authorization") String token,
            @PathVariable PaymentMethod paymentMethod,
            @PathVariable Long amount
            ) throws Exception {

        User user = userService.findUserProfileByJwt(token);

        PaymentResponse paymentResponse;

        PaymentOrder order = paymentService.createOrder(user, amount, paymentMethod);

        if(paymentMethod.equals(PaymentMethod.RAZORPAY)) {
            paymentResponse = paymentService.createRazorpayPaymentLing(user, amount);
        } else {
            paymentResponse = paymentService.createStripePaymentLing(user, amount, order.getId());
        }

        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }
}

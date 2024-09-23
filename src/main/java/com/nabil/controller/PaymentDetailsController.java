package com.nabil.controller;

import com.nabil.model.PaymentDetails;
import com.nabil.model.User;
import com.nabil.service.PaymentDetailsService;
import com.nabil.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment-details")
@RequiredArgsConstructor
public class PaymentDetailsController {

    private final UserService userService;

    private final PaymentDetailsService paymentDetailsService;

    @PostMapping
    public ResponseEntity<PaymentDetails> addPaymentDetails(
            @RequestHeader("Authorization") String token,
            @RequestBody PaymentDetails req) throws Exception {
        User user = userService.findUserProfileByJwt(token);
        PaymentDetails paymentDetails = paymentDetailsService.addPaymentDetails(
                req.getAccountNumber(),
                req.getAccountHolderName(),
                req.getBankName(),
                req.getIfsc(),
                user
        );

        return new ResponseEntity<>(paymentDetails, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaymentDetails> getUsersPaymentDetails(
            @RequestHeader("Authorization") String token) throws Exception {
        User user = userService.findUserProfileByJwt(token);
        PaymentDetails paymentDetails = paymentDetailsService.getusersPaymentDetails(user);

        return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
    }
}

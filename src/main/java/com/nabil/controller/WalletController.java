package com.nabil.controller;

import com.nabil.model.*;
import com.nabil.response.PaymentResponse;
import com.nabil.service.OrderService;
import com.nabil.service.PaymentService;
import com.nabil.service.UserService;
import com.nabil.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    private final UserService userService;

    private final OrderService orderService;

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String token) throws Exception {
        User user = userService.findUserProfileByJwt(token);
        Wallet wallet = walletService.getUserWallet(user);

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{walletId}/transfer")
    public ResponseEntity<Wallet> walletToWalletTransaction(
            @RequestHeader("Authorization") String token,
            @PathVariable Long walletId,
            @RequestBody WalletTransaction walletTransaction
            ) throws Exception {
        User sender = userService.findUserProfileByJwt(token);
        Wallet walletReceiver = walletService.findWalletById(walletId);

        Wallet walletSender = walletService.walletToWalletTransfer(
                sender, walletReceiver, walletTransaction.getAmount());

        return new ResponseEntity<>(walletSender, HttpStatus.ACCEPTED);

    }

    @PutMapping("/order/{orderId}/pay")
    public ResponseEntity<Wallet> payOrderPayment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId
    ) throws Exception {
        User user = userService.findUserProfileByJwt(token);

        Order order = orderService.getOrderById(orderId);

        Wallet wallet = walletService.payOrderPayment(order, user);

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);

    }

    @PutMapping("/deposit")
    public ResponseEntity<Wallet> addBalanceToWallet(
            @RequestHeader("Authorization") String token,
            @RequestParam(name="order_id") Long orderId,
            @RequestParam(name="payment_id") String paymentId
    ) throws Exception {

        User user = userService.findUserProfileByJwt(token);

        Wallet wallet = walletService.getUserWallet(user);

        PaymentOrder order = paymentService.getPaymentOrderById(orderId);

        Boolean status = paymentService.proceedPaymentOrder(order, paymentId);

        if(status) wallet = walletService.addBalance(wallet, order.getAmount());

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);

    }

}

package com.nabil.controller;

import com.nabil.model.Order;
import com.nabil.model.User;
import com.nabil.model.Wallet;
import com.nabil.model.WalletTransaction;
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

}

package com.nabil.controller;

import com.nabil.model.User;
import com.nabil.model.Wallet;
import com.nabil.model.Withdrawal;
import com.nabil.service.UserService;
import com.nabil.service.WalletService;
import com.nabil.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/withdrawal")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;
    private final WalletService walletService;
    private final UserService userService;

    @PostMapping("/{amount}")
    public ResponseEntity<?> requestWithdrawal(
            @RequestHeader("Authorization") String token,
            @PathVariable Long amount
    ) throws Exception {
        User user = userService.findUserProfileByJwt(token);
        Wallet wallet = walletService.getUserWallet(user);

        Withdrawal withdrawal = withdrawalService.requestWithdrawal(amount, user);
        walletService.addBalance(wallet, -withdrawal.getAmount());

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @PatchMapping("/admin/{id}/proceed/{accept}")
    public ResponseEntity<?> proceedWithWithdrawal(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @PathVariable boolean accept
    ) throws Exception {
        User user = userService.findUserProfileByJwt(token);
        Wallet wallet = walletService.getUserWallet(user);

        Withdrawal withdrawal = withdrawalService.proccedWithWithdrawal(id, accept);

        if(accept) {
            walletService.addBalance(wallet, withdrawal.getAmount());
        }

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Withdrawal>> getWithdrawalHistory(@RequestHeader("Authorization") String token) throws Exception {
        User user = userService.findUserProfileByJwt(token);

        List<Withdrawal> withdrawals = withdrawalService.getUsersWithdrawalHistory(user);


        return new ResponseEntity<>(withdrawals, HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawal() throws Exception {

        List<Withdrawal> withdrawals = withdrawalService.getAllWithdrawalRequest();

        return new ResponseEntity<>(withdrawals, HttpStatus.OK);
    }


}

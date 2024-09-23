package com.nabil.service;

import com.nabil.model.Order;
import com.nabil.model.User;
import com.nabil.model.Wallet;

public interface WalletService {

    Wallet getUserWallet(User user);
    Wallet addBalance(Wallet wallet, Long amount);
    Wallet findWalletById(Long id) throws Exception;
    Wallet walletToWalletTransfer(User sender, Wallet walletReceiver, Long amount) throws Exception;
    Wallet payOrderPayment(Order order, User user) throws Exception;
}

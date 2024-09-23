package com.nabil.service.impl;

import com.nabil.domain.OrderType;
import com.nabil.model.Order;
import com.nabil.model.User;
import com.nabil.model.Wallet;
import com.nabil.repository.WalletRepository;
import com.nabil.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet = walletRepository.findByUser(user);

        if(wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
        }
        return wallet;
    }

    @Override
    public Wallet addBalance(Wallet wallet, Long amount) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal newBalance = balance.add(BigDecimal.valueOf(amount));
        wallet.setBalance(newBalance);

        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findWalletById(Long id) throws Exception {
        Optional<Wallet> wallet = walletRepository.findById(id);

        if(wallet.isPresent()) return wallet.get();

        throw new Exception("Wallet not found.");
    }

    @Override
    public Wallet walletToWalletTransfer(User sender, Wallet walletReceiver, Long amount) throws Exception {
        Wallet walletSender = getUserWallet(sender);
        if(walletSender.getBalance().subtract(BigDecimal.valueOf(amount)).intValue() >= 0) {

            walletSender.setBalance(walletSender.getBalance().subtract(BigDecimal.valueOf(amount)));
            walletRepository.save(walletSender);

            walletReceiver.setBalance(walletReceiver.getBalance().add(BigDecimal.valueOf(amount)));
            walletRepository.save(walletReceiver);

            return walletSender;
        }
        throw new Exception("Insufficient balance");
    }

    @Override
    public Wallet payOrderPayment(Order order, User user) throws Exception {
        Wallet wallet = getUserWallet(user);
        BigDecimal newBalance;
        if(order.getOrderType().equals(OrderType.BUY)) {
            newBalance = wallet.getBalance().subtract(order.getPrice());

            if(newBalance.compareTo(order.getPrice()) < 0) {
                throw new Exception("Insufficient funds for this transaction.");
            }
        } else {
            newBalance = wallet.getBalance().add(order.getPrice());
        }
        wallet.setBalance(newBalance);

        walletRepository.save(wallet);
        return wallet;
    }
}

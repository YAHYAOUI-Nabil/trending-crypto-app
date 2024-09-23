package com.nabil.repository;

import com.nabil.model.User;
import com.nabil.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByUser(User user);
}

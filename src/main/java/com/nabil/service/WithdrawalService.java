package com.nabil.service;

import com.nabil.model.User;
import com.nabil.model.Withdrawal;

import java.util.List;

public interface WithdrawalService {

    Withdrawal requestWithdrawal(Long amount, User user);
    Withdrawal proccedWithWithdrawal(Long withdrawalId, boolean accept) throws Exception;
    List<Withdrawal> getUsersWithdrawalHistory(User user);
    List<Withdrawal> getAllWithdrawalRequest();
}

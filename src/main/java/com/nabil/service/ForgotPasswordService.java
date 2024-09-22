package com.nabil.service;

import com.nabil.domain.VerificationType;
import com.nabil.model.ForgotPasswordToken;
import com.nabil.model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user, String id, String otp, VerificationType verificationType, String sendTo);

    ForgotPasswordToken findById(String id) throws Exception;

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);
}

package com.nabil.service;

import com.nabil.domain.VerificationType;
import com.nabil.model.User;
import com.nabil.model.VerificationCode;

public interface VerificationCodeService {

    VerificationCode sendVerificationCode(User user, VerificationType verificationType);
    VerificationCode getVerificationCodeById(Long id);
    VerificationCode getVerificationCodeByUser(Long userId);
    void deleteVerificationCode(VerificationCode verificationCode);
}

package com.nabil.service.impl;

import com.nabil.domain.VerificationType;
import com.nabil.model.ForgotPasswordToken;
import com.nabil.model.User;
import com.nabil.repository.ForgotPasswordRepository;
import com.nabil.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private ForgotPasswordRepository forgotPasswordRepository;

    @Override
    public ForgotPasswordToken createToken(User user, String id, String otp, VerificationType verificationType, String sendTo) {
        ForgotPasswordToken token = new ForgotPasswordToken();

        token.setUser(user);
        token.setOtp(otp);
        token.setSendTo(sendTo);
        token.setVerificationType(verificationType);
        token.setId(id);

        return forgotPasswordRepository.save(token);
    }

    @Override
    public ForgotPasswordToken findById(String id) throws Exception {
        Optional<ForgotPasswordToken> forgotPasswordToken = forgotPasswordRepository.findById(id);

        if(forgotPasswordToken.isPresent()) {
            return forgotPasswordToken.get();
        }

        throw new Exception("token does not exist");
    }

    @Override
    public ForgotPasswordToken findByUser(Long userId) {
        return forgotPasswordRepository.findByUser(userId);
    }

    @Override
    public void deleteToken(ForgotPasswordToken token) {
        forgotPasswordRepository.delete(token);
    }
}

package com.nabil.controller;

import com.nabil.config.JwtProvider;
import com.nabil.domain.VerificationType;
import com.nabil.model.User;
import com.nabil.model.VerificationCode;
import com.nabil.service.EmailService;
import com.nabil.service.ForgotPasswordService;
import com.nabil.service.UserService;
import com.nabil.service.VerificationCodeService;
import com.nabil.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final EmailService emailService;

    private final VerificationCodeService verificationCodeService;

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> getUserByEmail(String email) throws Exception {
        User user = userService.findUserByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> getUserById(Long userId) throws Exception {
        User user = userService.findUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(
            @RequestHeader("Authorization") String jwt,
            @PathVariable VerificationType verificationType) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if(verificationCode == null) {
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }

        if(verificationType.equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }
        return new ResponseEntity<>("Verification code sent successfully.", HttpStatus.OK);
    }

    @PatchMapping("/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(
            @PathVariable String otp,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL)
                ? verificationCode.getEmail()
                : verificationCode.getMobile();
        boolean isVerified = verificationCode.getOtp().equals(otp);

        if(isVerified) {
            User updatedUser = userService.enableTwoFactorAuthentication(
                    verificationCode.getVerificationType(), sendTo, user);

            verificationCodeService.deleteVerificationCode(verificationCode);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }

        throw  new Exception("invalid otp.");
    }

    public ResponseEntity<User> updatePassword(User user, String password) {
        User updatedUser = userService.updatePassword(user, password);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/reset-password/send-otp")
    public ResponseEntity<String> sendForgotPasswordOtp(
            @RequestHeader("Authorization") String jwt,
            @PathVariable VerificationType verificationType) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        String otp = OtpUtils.generateOtp();

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        return new ResponseEntity<>("Forgot password otp sent successfully.", HttpStatus.OK);
    }
}

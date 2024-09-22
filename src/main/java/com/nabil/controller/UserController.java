package com.nabil.controller;

import com.nabil.request.ForgotPasswordTokenRequest;
import com.nabil.domain.VerificationType;
import com.nabil.model.ForgotPasswordToken;
import com.nabil.model.User;
import com.nabil.model.VerificationCode;
import com.nabil.request.ResetPasswordRequest;
import com.nabil.response.ApiResponse;
import com.nabil.response.AuthResponse;
import com.nabil.service.EmailService;
import com.nabil.service.ForgotPasswordService;
import com.nabil.service.UserService;
import com.nabil.service.VerificationCodeService;
import com.nabil.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(
            @RequestBody ForgotPasswordTokenRequest req) throws Exception {

        User user = userService.findUserProfileByJwt(req.getSendTo());
        String otp = OtpUtils.generateOtp();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgotPasswordToken token = forgotPasswordService.findByUser(user.getId());

        if(token == null) {
            token = forgotPasswordService.createToken(user, id, otp, req.getVerificationType(), req.getSendTo());
        }

        if(req.getVerificationType().equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOtpEmail(user.getEmail(), token.getOtp());
        }

        AuthResponse response = new AuthResponse();

        response.setSession(token.getId());
        response.setMessage("Password reset otp sent successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestParam String id, @RequestBody ResetPasswordRequest req) throws Exception {

        ForgotPasswordToken token = forgotPasswordService.findById(id);
        boolean isVerified = token.getOtp().equals(req.getOtp());

        if(isVerified) {
            userService.updatePassword(token.getUser(), req.getPassword());
            ApiResponse response = new ApiResponse();

            response.setMessage("Password updated successfully.");

            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }

        throw new Exception("Wrong otp.");

    }
}

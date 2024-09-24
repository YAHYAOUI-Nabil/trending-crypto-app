package com.nabil.controller;

import com.nabil.config.JwtProvider;
import com.nabil.model.TwoFactorOTP;
import com.nabil.model.User;
import com.nabil.repository.UserRepository;
import com.nabil.response.AuthResponse;
import com.nabil.service.CustomUserDetailsService;
import com.nabil.service.EmailService;
import com.nabil.service.TwoFactorOtpService;
import com.nabil.service.WatchListService;
import com.nabil.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final TwoFactorOtpService twoFactorOtpService;
    private final EmailService emailService;
    private final WatchListService watchListService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {

        User isEmailExist = userRepository.findByEmail(user.getEmail());

        if(isEmailExist != null) {
            throw new Exception("User already exists");
        }

        User newUser = new User();

        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());

        User savedUser = userRepository.save(newUser);

        watchListService.createUserWatchList(savedUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        AuthResponse response = new AuthResponse();

        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("User registered successfully.");
        response.setTwoFactorAuthEnabled(false);

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String email = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(email, password);

        User authUser = userRepository.findByEmail(email);

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        if(user.getTwoFactorAuth().isEnabled()) {
            AuthResponse response = new AuthResponse();

            response.setMessage("Two factor auth is enabled.");
            response.setTwoFactorAuthEnabled(true);
            String otp = OtpUtils.generateOtp();

            TwoFactorOTP oldTwoFactorOTP = twoFactorOtpService.findByUser(authUser.getId());
            if(oldTwoFactorOTP != null) {
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOTP);
            }
            TwoFactorOTP newTwoFactorOTP = twoFactorOtpService.createTwoFactorOTP(authUser, otp, jwt);

            emailService.sendVerificationOtpEmail(email, otp);

            response.setSession(newTwoFactorOTP.getOtp());

            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }

        AuthResponse response = new AuthResponse();

        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("User logged in successfully.");
        response.setTwoFactorAuthEnabled(false);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySignInOtp(@RequestBody String otp, @RequestParam String id) throws Exception {
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findById(id);

        if (twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP, otp)) {
            AuthResponse response = new AuthResponse();

            response.setMessage("Two factor OTP verified");
            response.setTwoFactorAuthEnabled(true);
            response.setJwt(twoFactorOTP.getJwt());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        throw new Exception("Invalid OTP");
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if(userDetails == null) {
            throw new BadCredentialsException("User does not exist");
        }

        if(!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Wrong password!");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
}

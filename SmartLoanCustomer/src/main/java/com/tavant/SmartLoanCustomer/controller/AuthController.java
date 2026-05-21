package com.tavant.SmartLoanCustomer.controller;

import com.tavant.SmartLoanCustomer.dto.ApiResponse;
import com.tavant.SmartLoanCustomer.dto.LoginRequest;
import com.tavant.SmartLoanCustomer.dto.LoginResponse;
import com.tavant.SmartLoanCustomer.dto.PasswordResetRequest;
import com.tavant.SmartLoanCustomer.dto.PasswordResetSubmitRequest;
import com.tavant.SmartLoanCustomer.dto.RegisterRequest;
import com.tavant.SmartLoanCustomer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomerService customerService;

    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        customerService.register(request);
        return ResponseEntity.ok(new ApiResponse("Registration successful. Verify your email using the verification token from the customer record."));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = customerService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam("token") String token) {
        customerService.verifyEmail(token);
        return ResponseEntity.ok(new ApiResponse("Email verification completed."));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<ApiResponse> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        String resetToken = customerService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(new ApiResponse("Password reset token created.", resetToken));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody PasswordResetSubmitRequest request) {
        customerService.resetPassword(request.getResetToken(), request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse("Password reset successful."));
    }
}

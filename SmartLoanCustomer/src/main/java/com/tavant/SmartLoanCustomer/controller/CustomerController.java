package com.tavant.SmartLoanCustomer.controller;

import com.tavant.SmartLoanCustomer.dto.ProfileResponse;
import com.tavant.SmartLoanCustomer.dto.UpdateProfileRequest;
import com.tavant.SmartLoanCustomer.exception.UnauthorizedException;
import com.tavant.SmartLoanCustomer.model.Customer;
import com.tavant.SmartLoanCustomer.service.CustomerService;
import com.tavant.SmartLoanCustomer.service.SessionTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final SessionTokenService sessionTokenService;

    public CustomerController(CustomerService customerService, SessionTokenService sessionTokenService) {
        this.customerService = customerService;
        this.sessionTokenService = sessionTokenService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> profile(@RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
        Customer customer = sessionTokenService.validateBearerToken(authorizationHeader);
        ProfileResponse response = customerService.getProfile(customer.getCustomerId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
                                                         @Valid @RequestBody UpdateProfileRequest request) {
        Customer customer = sessionTokenService.validateBearerToken(authorizationHeader);
        if (customer == null) {
            throw new UnauthorizedException("Unable to authenticate customer");
        }
        ProfileResponse updated = customerService.updateProfile(customer.getCustomerId(), request);
        return ResponseEntity.ok(updated);
    }
}

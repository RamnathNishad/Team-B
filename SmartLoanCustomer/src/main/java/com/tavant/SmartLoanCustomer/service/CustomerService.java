package com.tavant.SmartLoanCustomer.service;

import com.tavant.SmartLoanCustomer.dto.LoginRequest;
import com.tavant.SmartLoanCustomer.dto.LoginResponse;
import com.tavant.SmartLoanCustomer.dto.ProfileResponse;
import com.tavant.SmartLoanCustomer.dto.RegisterRequest;
import com.tavant.SmartLoanCustomer.dto.UpdateProfileRequest;
import com.tavant.SmartLoanCustomer.exception.InvalidRequestException;
import com.tavant.SmartLoanCustomer.exception.ResourceNotFoundException;
import com.tavant.SmartLoanCustomer.model.AccountStatus;
import com.tavant.SmartLoanCustomer.model.Customer;
import com.tavant.SmartLoanCustomer.model.LoginHistory;
import com.tavant.SmartLoanCustomer.model.SessionToken;
import com.tavant.SmartLoanCustomer.repository.CustomerRepository;
import com.tavant.SmartLoanCustomer.repository.LoginHistoryRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final SessionTokenService sessionTokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CustomerService(CustomerRepository customerRepository,
                           LoginHistoryRepository loginHistoryRepository,
                           SessionTokenService sessionTokenService) {
        this.customerRepository = customerRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.sessionTokenService = sessionTokenService;
    }

    @Transactional
    public Customer register(RegisterRequest request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidRequestException("Email already exists");
        }

        Customer customer = new Customer();
        customer.setEmail(request.getEmail().trim().toLowerCase());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setFirstName(request.getFirstName().trim());
        customer.setLastName(request.getLastName().trim());
        customer.setDob(request.getDob());
        customer.setPhoneNumber(request.getPhoneNumber().trim());
        customer.setAddress(request.getAddress().trim());
        customer.setAccountStatus(AccountStatus.Active);
        customer.setEmailVerified(false);
        customer.setVerificationToken(UUID.randomUUID().toString());
        customer.setCreatedDate(LocalDateTime.now());

        return customerRepository.save(customer);
    }

    @Transactional
    public LoginResponse authenticate(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new InvalidRequestException("Invalid email or password"));

        boolean validPassword = passwordEncoder.matches(request.getPassword(), customer.getPasswordHash());
        saveLoginHistory(customer, validPassword);

        if (!validPassword) {
            throw new InvalidRequestException("Invalid email or password");
        }

        if (!customer.isEmailVerified()) {
            throw new InvalidRequestException("Email address is not verified");
        }

        customer.setLastLoginAt(LocalDateTime.now());
        customerRepository.save(customer);

        SessionToken sessionToken = sessionTokenService.createToken(customer, request.isRememberMe());
        return new LoginResponse(sessionToken.getToken(), sessionToken.getExpiresAt(),
                customer.getEmail(), customer.getFirstName(), customer.getLastName());
    }

    @Transactional
    public void verifyEmail(String verificationToken) {
        Customer customer = customerRepository.findByVerificationToken(verificationToken)
                .orElseThrow(() -> new ResourceNotFoundException("Verification token not found"));
        customer.setEmailVerified(true);
        customerRepository.save(customer);
    }

    @Transactional
    public String requestPasswordReset(String email) {
        Customer customer = customerRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Email address not found"));

        customer.setResetToken(UUID.randomUUID().toString());
        customerRepository.save(customer);

        return customer.getResetToken();
    }

    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        Customer customer = customerRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new ResourceNotFoundException("Reset token not found"));

        customer.setPasswordHash(passwordEncoder.encode(newPassword));
        customer.setResetToken(null);
        customerRepository.save(customer);
    }

    public ProfileResponse getProfile(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
        return buildProfileResponse(customer);
    }

    @Transactional
    public ProfileResponse updateProfile(UUID customerId, UpdateProfileRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        customer.setFirstName(request.getFirstName().trim());
        customer.setLastName(request.getLastName().trim());
        customer.setDob(request.getDob());
        customer.setPhoneNumber(request.getPhoneNumber().trim());
        customer.setAddress(request.getAddress().trim());

        Customer updated = customerRepository.save(customer);
        return buildProfileResponse(updated);
    }

    private void saveLoginHistory(Customer customer, boolean success) {
        LoginHistory history = new LoginHistory(customer, success);
        loginHistoryRepository.save(history);
    }

    private ProfileResponse buildProfileResponse(Customer customer) {
        ProfileResponse response = new ProfileResponse();
        response.setCustomerId(customer.getCustomerId());
        response.setEmail(customer.getEmail());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setDob(customer.getDob());
        response.setPhoneNumber(customer.getPhoneNumber());
        response.setAddress(customer.getAddress());
        response.setAccountStatus(customer.getAccountStatus().name());
        response.setEmailVerified(customer.isEmailVerified());
        response.setLastLoginAt(customer.getLastLoginAt());
        response.setCreatedDate(customer.getCreatedDate());
        return response;
    }
}

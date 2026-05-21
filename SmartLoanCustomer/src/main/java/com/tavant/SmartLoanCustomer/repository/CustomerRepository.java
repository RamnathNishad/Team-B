package com.tavant.SmartLoanCustomer.repository;

import com.tavant.SmartLoanCustomer.model.Customer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByVerificationToken(String verificationToken);
    Optional<Customer> findByResetToken(String resetToken);
}

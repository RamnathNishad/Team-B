package com.tavant.SmartLoanCustomer.repository;

import com.tavant.SmartLoanCustomer.model.SessionToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionTokenRepository extends JpaRepository<SessionToken, String> {
    Optional<SessionToken> findByToken(String token);
}

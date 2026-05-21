package com.tavant.SmartLoanCustomer.service;

import com.tavant.SmartLoanCustomer.exception.UnauthorizedException;
import com.tavant.SmartLoanCustomer.model.Customer;
import com.tavant.SmartLoanCustomer.model.SessionToken;
import com.tavant.SmartLoanCustomer.repository.SessionTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SessionTokenService {

    private static final long STANDARD_EXPIRATION_MINUTES = 60;
    private static final long REMEMBER_ME_EXPIRATION_DAYS = 30;

    private final SessionTokenRepository sessionTokenRepository;

    public SessionTokenService(SessionTokenRepository sessionTokenRepository) {
        this.sessionTokenRepository = sessionTokenRepository;
    }

    public SessionToken createToken(Customer customer, boolean rememberMe) {
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = rememberMe
                ? now.plusDays(REMEMBER_ME_EXPIRATION_DAYS)
                : now.plusMinutes(STANDARD_EXPIRATION_MINUTES);

        SessionToken sessionToken = new SessionToken(token, customer, expiresAt, rememberMe);
        sessionToken.setCreatedAt(now);
        sessionToken.setLastActiveAt(now);

        return sessionTokenRepository.save(sessionToken);
    }

    public Optional<SessionToken> findByToken(String token) {
        return sessionTokenRepository.findByToken(token);
    }

    public Customer validateBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new UnauthorizedException("Bearer token is empty");
        }

        SessionToken sessionToken = findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Session token is not valid"));

        if (sessionToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Session token has expired");
        }

        sessionToken.setLastActiveAt(LocalDateTime.now());
        sessionTokenRepository.save(sessionToken);

        return sessionToken.getCustomer();
    }
}

package com.tavant.SmartLoanCustomer.service;

import com.tavant.SmartLoanCustomer.dto.LoginRequest;
import com.tavant.SmartLoanCustomer.dto.LoginResponse;
import com.tavant.SmartLoanCustomer.dto.RegisterRequest;
import com.tavant.SmartLoanCustomer.exception.InvalidRequestException;
import com.tavant.SmartLoanCustomer.model.AccountStatus;
import com.tavant.SmartLoanCustomer.model.Customer;
import com.tavant.SmartLoanCustomer.model.SessionToken;
import com.tavant.SmartLoanCustomer.repository.CustomerRepository;
import com.tavant.SmartLoanCustomer.repository.LoginHistoryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @Mock
    private SessionTokenService sessionTokenService;

    @InjectMocks
    private CustomerService customerService;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("StrongPass123");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setDob(LocalDate.of(1990, 1, 15));
        registerRequest.setPhoneNumber("+1-555-0142");
        registerRequest.setAddress("123 Elm Street");
    }

    @Test
    void register_createsCustomerWhenEmailDoesNotExist() {
        when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer customer = customerService.register(registerRequest);

        assertNotNull(customer);
        assertEquals("john.doe@example.com", customer.getEmail());
        assertTrue(customer.getPasswordHash().length() > 0);
        assertEquals("John", customer.getFirstName());
        assertEquals(AccountStatus.Active, customer.getAccountStatus());
        assertFalse(customer.isEmailVerified());
        verify(customerRepository).save(customerCaptor.capture());
        assertEquals("john.doe@example.com", customerCaptor.getValue().getEmail());
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(new Customer()));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> customerService.register(registerRequest));

        assertEquals("Email already exists", exception.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void authenticate_returnsResponseWhenCredentialsValid() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID());
        customer.setEmail("john.doe@example.com");
        customer.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("StrongPass123"));
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmailVerified(true);
        customer.setAccountStatus(AccountStatus.Active);

        when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(customer));
        when(sessionTokenService.createToken(any(Customer.class), eq(false)))
                .thenReturn(new SessionToken("test-token", customer, LocalDateTime.now().plusMinutes(60), false));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("StrongPass123");

        LoginResponse response = customerService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        verify(loginHistoryRepository).save(any());
        verify(customerRepository).save(customer);
    }

    @Test
    void authenticate_throwsWhenPasswordInvalid() {
        Customer customer = new Customer();
        customer.setEmail("john.doe@example.com");
        customer.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("StrongPass123"));
        customer.setEmailVerified(true);

        when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(customer));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("WrongPassword");

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> customerService.authenticate(loginRequest));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(loginHistoryRepository).save(any());
        verify(sessionTokenService, never()).createToken(any(), anyBoolean());
    }
}

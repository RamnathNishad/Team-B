package com.smartLoan.controller;

import com.smartLoan.entity.LoanApplication;
import com.smartLoan.exception.GlobalExceptionHandler;
import com.smartLoan.service.LoanApplicationService;
import com.smartLoan.validation.LoanApplicationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LoanApplicationControllerValidationTest {

    @Mock
    private LoanApplicationService loanApplicationService;

    @Mock
    private LoanApplicationValidator loanApplicationValidator;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        LoanApplicationController controller = new LoanApplicationController(
                loanApplicationService,
                loanApplicationValidator);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldReturnBadRequestWhenCustomerIdIsInvalid() throws Exception {
        mockMvc.perform(multipart("/api/v1/loan-applications/applyLoan")
                        .param("customerId", "0")
                        .param("loanType", "Home")
                        .param("amount", "500000.00")
                        .param("tenure", "60")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("customerId: customerId must be greater than 0"));
    }

    @Test
    void shouldReturnBadRequestWhenStatusUpdateRequestIsBlank() throws Exception {
        mockMvc.perform(put("/api/v1/loan-applications/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("status: status is required"));
    }

    @Test
    void shouldCreateLoanApplicationWhenBeanValidationPasses() throws Exception {
        when(loanApplicationService.createLoanApplication(any())).thenReturn(
                LoanApplication.builder()
                        .applicationId(1L)
                        .customerId(1L)
                        .loanType("Home")
                        .build());

        mockMvc.perform(multipart("/api/v1/loan-applications/applyLoan")
                        .param("customerId", "1")
                        .param("loanType", "Home")
                        .param("amount", "500000.00")
                        .param("tenure", "60")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.applicationId").value(1))
                .andExpect(jsonPath("$.loanType").value("Home"));
    }
}


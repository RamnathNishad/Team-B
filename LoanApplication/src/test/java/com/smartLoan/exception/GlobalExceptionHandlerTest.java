package com.smartLoan.exception;

import com.smartLoan.validation.LoanApplicationValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestExceptionController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnBadRequestForValidationException() throws Exception {
        mockMvc.perform(get("/test/validation").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("loanType must not be blank"))
                .andExpect(jsonPath("$.path").value("/test/validation"));
    }

    @Test
    void shouldReturnNotFoundForEntityNotFoundException() throws Exception {
        mockMvc.perform(get("/test/not-found").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Loan application not found for applicationId: 99"));
    }

    @Test
    void shouldReturnConflictForDataConflictException() throws Exception {
        mockMvc.perform(get("/test/conflict").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Unable to create the loan application because the request conflicts with database constraints."));
    }

    @Test
    void shouldReturnServiceUnavailableForDatabaseUnavailableException() throws Exception {
        mockMvc.perform(get("/test/database-unavailable").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.error").value("Service Unavailable"))
                .andExpect(jsonPath("$.message").value("Unable to fetch the loan application because the database is currently unavailable. Please try again later."));
    }

    @Test
    void shouldReturnInternalServerErrorForGenericDatabaseOperationException() throws Exception {
        mockMvc.perform(get("/test/database-error").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Unable to fetch the loan application due to a database error. Please contact support if the issue persists."));
    }

    @RestController
    @RequestMapping("/test")
    static class TestExceptionController {

        @GetMapping("/validation")
        String validation() {
            throw new LoanApplicationValidationException("loanType must not be blank");
        }

        @GetMapping("/not-found")
        String notFound() {
            throw new EntityNotFoundException("Loan application not found for applicationId: 99");
        }

        @GetMapping("/conflict")
        String conflict() {
            throw new DataConflictException(
                    "Unable to create the loan application because the request conflicts with database constraints.",
                    new RuntimeException("duplicate key"));
        }

        @GetMapping("/database-unavailable")
        String databaseUnavailable() {
            throw new DatabaseUnavailableException(
                    "Unable to fetch the loan application because the database is currently unavailable. Please try again later.",
                    new RuntimeException("connection failure"));
        }

        @GetMapping("/database-error")
        String databaseError() {
            throw new DatabaseOperationException(
                    "Unable to fetch the loan application due to a database error. Please contact support if the issue persists.",
                    new RuntimeException("sql grammar"));
        }
    }
}


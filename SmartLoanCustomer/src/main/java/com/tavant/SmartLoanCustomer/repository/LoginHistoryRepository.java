package com.tavant.SmartLoanCustomer.repository;

import com.tavant.SmartLoanCustomer.model.LoginHistory;
import com.tavant.SmartLoanCustomer.model.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByCustomerOrderByLoginAtDesc(Customer customer);
}

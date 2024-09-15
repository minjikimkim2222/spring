package com.minjiki2.section9.repository;

import com.minjiki2.section9.model.AccountTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountTransactionsRepository extends JpaRepository<AccountTransactions, String> {
    List<AccountTransactions> findByCustomerIdOrderByTransactionDtDesc(long customerId);
}

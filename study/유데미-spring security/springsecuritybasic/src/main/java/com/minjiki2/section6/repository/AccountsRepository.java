package com.minjiki2.section6.repository;

import com.minjiki2.section6.model.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
    Accounts findByCustomerId(long customerId);
}

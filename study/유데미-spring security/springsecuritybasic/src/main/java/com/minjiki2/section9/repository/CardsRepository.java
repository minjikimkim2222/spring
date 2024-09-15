package com.minjiki2.section9.repository;

import com.minjiki2.section9.model.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardsRepository extends JpaRepository<Cards, Long> {
    List<Cards> findByCustomerId(long customerId);
}

package org.example.bankcardmanagement.repository;

import org.example.bankcardmanagement.model.Card;
import org.example.bankcardmanagement.model.CardStatus;
import org.example.bankcardmanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByIsBlockedTrue(Pageable pageable);
    Page<Card> findByUser(User user, Pageable pageable);
    List<Card> findByUserAndIsBlockedFalse(User user);
    List<Card> findByUserAndStatusAndIsBlockedFalse(User user, CardStatus status);
}

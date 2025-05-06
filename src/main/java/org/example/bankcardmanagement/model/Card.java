package org.example.bankcardmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardHolderName;

    @Column(unique = true)
    private String encryptedCardNumber;

    private LocalDate expirationDate;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private boolean isBlocked = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

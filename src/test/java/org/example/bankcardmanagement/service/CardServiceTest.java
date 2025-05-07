package org.example.bankcardmanagement.service;

import org.example.bankcardmanagement.dto.CardResponse;
import org.example.bankcardmanagement.dto.PageDto;
import org.example.bankcardmanagement.dto.TransferRequest;
import org.example.bankcardmanagement.model.Card;
import org.example.bankcardmanagement.model.CardStatus;
import org.example.bankcardmanagement.model.Role;
import org.example.bankcardmanagement.model.User;
import org.example.bankcardmanagement.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardService cardService;

    private User testUser;
    private Card testCard;
    private PageDto pageDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        Role userRole = new Role();
        userRole.setRoleName("ROLE_USER");
        Role adminRole = new Role();
        adminRole.setRoleName("ROLE_ADMIN");

        testUser.setRoles(new HashSet<>(Arrays.asList(userRole)));

        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardHolderName("Test User");
        testCard.setEncryptedCardNumber("encrypted123456789012");
        testCard.setExpirationDate(LocalDate.now().plusYears(3));
        testCard.setBalance(BigDecimal.valueOf(1000));
        testCard.setStatus(CardStatus.ACTIVE);
        testCard.setUser(testUser);
        testCard.setBlocked(false);

        pageDto = new PageDto(0, 10, "id", "ASC");
    }

    @Test
    void getCardById_ShouldReturnCard_WhenCardExists() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

        Card result = cardService.getCardById(1L);

        assertNotNull(result);
        assertEquals(testCard.getId(), result.getId());
        verify(cardRepository).findById(1L);
    }

    @Test
    void getCardById_ShouldThrowException_WhenCardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cardService.getCardById(1L));
        verify(cardRepository).findById(1L);
    }

    @Test
    void createCard_ShouldCreateNewCard() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        CardResponse result = cardService.createCard("Test User");

        assertNotNull(result);
        assertEquals("Test User", result.cardHolderName());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void changeStatusCard_ShouldChangeStatusToActive() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        cardService.changeStatusCard(1L, "ACTIVE");

        assertEquals(CardStatus.ACTIVE, testCard.getStatus());
        verify(cardRepository).save(testCard);
    }

    @Test
    void changeStatusCard_ShouldChangeStatusToBlocked() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        cardService.changeStatusCard(1L, "BLOCKED");

        assertEquals(CardStatus.BLOCKED, testCard.getStatus());
        verify(cardRepository).save(testCard);
    }

    @Test
    void changeStatusCard_ShouldThrowException_WhenInvalidStatus() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

        assertThrows(IllegalArgumentException.class, () -> cardService.changeStatusCard(1L, "INVALID"));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void deleteCard_ShouldDeleteCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        doNothing().when(cardRepository).delete(testCard);

        cardService.deleteCard(1L);

        verify(cardRepository).delete(testCard);
    }

    @Test
    void requestBlockCard_ShouldSetBlockedTrue_WhenCardBelongsToUser() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(cardRepository.findByUserAndIsBlockedFalse(testUser)).thenReturn(Collections.singletonList(testCard));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        cardService.requestBlockCard(1L);

        assertTrue(testCard.isBlocked());
        verify(cardRepository).save(testCard);
    }

    @Test
    void requestBlockCard_ShouldThrowException_WhenCardNotBelongsToUser() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(cardRepository.findByUserAndIsBlockedFalse(testUser)).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> cardService.requestBlockCard(1L));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void transferFunds_ShouldTransferMoney_WhenCardsBelongToUserAndHaveEnoughMoney() {
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(BigDecimal.valueOf(1000));
        fromCard.setUser(testUser);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setBlocked(false);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(BigDecimal.valueOf(500));
        toCard.setUser(testUser);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBlocked(false);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(cardRepository.findByUserAndStatusAndIsBlockedFalse(testUser, CardStatus.ACTIVE))
                .thenReturn(Stream.of(fromCard, toCard).collect(Collectors.toList()));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        TransferRequest transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(300));
        cardService.transferFunds(transferRequest);

        assertEquals(BigDecimal.valueOf(700), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(800), toCard.getBalance());
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transferFunds_ShouldThrowException_WhenNotEnoughMoney() {
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(BigDecimal.valueOf(100));
        fromCard.setUser(testUser);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setBlocked(false);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(BigDecimal.valueOf(500));
        toCard.setUser(testUser);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBlocked(false);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(cardRepository.findByUserAndStatusAndIsBlockedFalse(testUser, CardStatus.ACTIVE))
                .thenReturn(Stream.of(fromCard, toCard).collect(Collectors.toList()));

        TransferRequest transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(300));

        assertThrows(IllegalArgumentException.class, () -> cardService.transferFunds(transferRequest));
        verify(cardRepository, never()).save(any());
    }


    @Test
    void transferFunds_ShouldThrowException_WhenCardsNotBelongToUser() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(cardRepository.findByUserAndStatusAndIsBlockedFalse(testUser, CardStatus.ACTIVE))
                .thenReturn(Collections.emptyList());

        TransferRequest transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(300));

        assertThrows(IllegalArgumentException.class, () -> cardService.transferFunds(transferRequest));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void generateCardNumber_ShouldGenerate16DigitNumber() {
        String cardNumber = cardService.generateCardNumber();

        assertNotNull(cardNumber);
        assertEquals(16, cardNumber.length());
        assertTrue(cardNumber.matches("\\d+"));
    }
}
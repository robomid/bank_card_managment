package org.example.bankcardmanagement.service;

import jakarta.transaction.Transactional;
import org.example.bankcardmanagement.dto.CardResponse;
import org.example.bankcardmanagement.dto.PageDto;
import org.example.bankcardmanagement.dto.TransferRequest;
import org.example.bankcardmanagement.mapper.CardMapper;
import org.example.bankcardmanagement.model.Card;
import org.example.bankcardmanagement.model.CardStatus;
import org.example.bankcardmanagement.model.Role;
import org.example.bankcardmanagement.model.User;
import org.example.bankcardmanagement.repository.CardRepository;
import org.example.bankcardmanagement.util.EncryptionUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final UserService userService;

    public CardService(CardRepository cardRepository, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    public Card getCardById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Card not found"));
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }

    private Pageable getPageable(PageDto pageDto) {
        Sort sort = Sort.by(Sort.Direction.fromString(pageDto.sortDirection()), pageDto.sortBy());
        return PageRequest.of(pageDto.pageNumber(), pageDto.pageSize(), sort);
    }

    @Transactional
    public Page<CardResponse> getCards(PageDto pageDto) {
        User currentUser = getCurrentUser();
        Set<String> roles = currentUser.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());

        if(roles.contains("ROLE_ADMIN")) {
            return cardRepository.findAll(getPageable(pageDto)).map(CardMapper.INSTANCE::toCardResponse);
        }

        Page<Card> cards = cardRepository.findByUser(currentUser, getPageable(pageDto));

        for (Card card : cards) {
            String number = card.getEncryptedCardNumber();
        }

        return cardRepository.findByUser(currentUser, getPageable(pageDto)).map(CardMapper.INSTANCE::toCardResponse);
    }

    public CardResponse createCard(String cardHolderName) {
        Card card = new Card();
        card.setCardHolderName(cardHolderName);
        card.setEncryptedCardNumber(EncryptionUtil.encrypt(generateCardNumber()));
        card.setExpirationDate(LocalDate.now().plusYears(3));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(CardStatus.ACTIVE);
        card.setUser(getCurrentUser());

        cardRepository.save(card);

        return CardMapper.INSTANCE.toCardResponse(card);
    }

    @Transactional
    public void changeStatusCard(Long cardId, String status) {
        Card card = getCardById(cardId);

        if(status.equals("ACTIVE")) {
            card.setStatus(CardStatus.ACTIVE);
        } else if(status.equals("BLOCKED")) {
            card.setStatus(CardStatus.BLOCKED);
        } else {
            throw new IllegalArgumentException("Status not found");
        }

        cardRepository.save(card);
    }

    @Transactional
    public void deleteCard(long cardId) {
        Card card = getCardById(cardId);
        cardRepository.delete(card);
    }

    public Page<CardResponse> getRequestBlockCards(PageDto pageDto) {
        return cardRepository.findByIsBlockedTrue(getPageable(pageDto)).map(CardMapper.INSTANCE::toCardResponse);
    }

    @Transactional
    public void requestBlockCard(long cardId) {
        User currentUser = getCurrentUser();
        List<Long> cards = cardRepository.findByUserAndIsBlockedFalse(currentUser).stream().map(Card::getId).toList();

        if(cards.contains(cardId)) {
            Card card = getCardById(cardId);
            card.setBlocked(true);
            cardRepository.save(card);
        } else {
            throw new IllegalArgumentException("Card not found");
        }
    }

    @Transactional
    public void transferFunds(TransferRequest transferRequest) {
        Long fromCardId = transferRequest.fromCardId();
        Long toCardId = transferRequest.toCardId();
        BigDecimal amount = transferRequest.amount();

        User currentUser = getCurrentUser();
        Set<Long> cards = cardRepository.findByUserAndStatusAndIsBlockedFalse(currentUser, CardStatus.ACTIVE).stream().map(Card::getId).collect(Collectors.toSet());

        if(cards.containsAll(Arrays.asList(fromCardId, toCardId))) {
            Card fromCard = getCardById(fromCardId);
            Card toCard = getCardById(toCardId);

            // Если денег достаточно для перевода
            if(fromCard.getBalance().compareTo(amount) >= 0) {
                fromCard.setBalance(fromCard.getBalance().subtract(amount));
                toCard.setBalance(toCard.getBalance().add(amount));
                cardRepository.save(fromCard);
                cardRepository.save(toCard);
            } else {
                throw new IllegalArgumentException("Not enough money to transfer.");
            }
        } else {
            throw new IllegalArgumentException("Card(s) cannot be used for the transfer.");
        }
    }

    public String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }

        return cardNumber.toString();
    }
}

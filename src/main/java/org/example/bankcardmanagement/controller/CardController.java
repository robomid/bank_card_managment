package org.example.bankcardmanagement.controller;

import jakarta.validation.Valid;
import org.example.bankcardmanagement.dto.PageDto;
import org.example.bankcardmanagement.dto.TransferRequest;
import org.example.bankcardmanagement.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<?> getCards(@RequestBody PageDto pageDto) {
        return ResponseEntity.ok(cardService.getCards(pageDto));
    }

    @PostMapping
    public ResponseEntity<?> addCard(@RequestParam String cardHolderName) {
        return ResponseEntity.ok(cardService.createCard(cardHolderName));
    }

    @PostMapping("/block/{cardId}")
    public ResponseEntity<?> blockingCard(@PathVariable long cardId) {
        cardService.blockCard(cardId);
        return ResponseEntity.ok("The card has been successfully blocked.");
    }

    @PostMapping("/active/{cardId}")
    public ResponseEntity<?> activeCard(@PathVariable long cardId) {
        cardService.activeCard(cardId);
        return ResponseEntity.ok("The card has been successfully activated.");
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.ok("The card has been successfully deleted.");
    }

    @GetMapping("/requesBlockCard/{cardId}")
    public ResponseEntity<?> requestBlockCard(@PathVariable long cardId) {
        cardService.requestBlockCard(cardId);
        return ResponseEntity.ok("The card has been successfully marked for blocking.");
    }

    @GetMapping("/requestBlockCard")
    public ResponseEntity<?> getRequestBlockCard(@RequestBody PageDto pageDto) {
        return ResponseEntity.ok(cardService.getRequestBlockCards(pageDto));
    }

    @PostMapping("/transferMoney")
    public ResponseEntity<?> transferMoney(@Valid @RequestBody TransferRequest transferRequest) {
        cardService.transferFunds(transferRequest);
        return ResponseEntity.ok("Transfer successful");
    }
}

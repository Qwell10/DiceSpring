package com.dice.controller;

import com.dice.dto.*;
import com.dice.service.GameService;
import com.dice.service.ScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dice")
public class DiceController {

    @Autowired
    private ScoringService scoringService;

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/roll")
    public ResponseEntity<?> rollDice() {
        List<Integer> rolledDice = scoringService.rollDice(gameService.prepareDiceForRoll());
        //  List<Integer> rolledDice = new ArrayList<>(List.of(1, 1, 5, 3, 3, 3));

        broadcastGameState(rolledDice);

        if (scoringService.isRollScorable(rolledDice)) {
            return ResponseEntity.ok().body(new RollResponse(rolledDice, false, null));
        } else {
            gameService.setActivePlayerRemainingDiceToSix();
            gameService.setActivePlayerTurnScore(0);
            gameService.switchPlayer();

            return ResponseEntity.ok().body(new RollResponse(rolledDice, true, "Bust! Nic nepadlo. Hraje druhý hráč."));
        }
    }

    @PostMapping("/score")
    public ResponseEntity<?> calculateScore(@RequestBody List<Integer> pickedDice) {
        if (scoringService.isLargeStraight(pickedDice)) {
            gameService.saveTurnScore(3000);
            return ResponseEntity.ok().body(new TurnStatusResponse(gameService.getTurnScore(), null));
        }

        if (scoringService.isSmallStraight(pickedDice)) {
            if (scoringService.containsOnes(pickedDice)) {
                gameService.saveTurnScore(1600);
                return ResponseEntity.ok().body(new TurnStatusResponse(gameService.getTurnScore(), null));
            } else if (scoringService.containsFives(pickedDice)) {
                gameService.saveTurnScore(1550);
                return ResponseEntity.ok().body(new TurnStatusResponse(gameService.getTurnScore(), null));
            } else {
                gameService.saveTurnScore(1500);
                return ResponseEntity.ok().body(new TurnStatusResponse(gameService.getTurnScore(), null));
            }
        }

        if (scoringService.hasInvalidDice(pickedDice)) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Neplatné kostky!"));
        }

        int turnScore = scoringService.calculateScore(pickedDice);
        gameService.saveTurnScore(turnScore);
        gameService.setActivePlayerRemainingDice(pickedDice);

        return ResponseEntity.ok().body(new TurnStatusResponse(gameService.getTurnScore(), null));
    }

    @PostMapping("/endTurn")
    public ResponseEntity<?> endTurn() {
        int totalScore = gameService.endTurn();

        if (totalScore >= 5000) {
            return ResponseEntity.ok().body(new EndTurnResponse(totalScore, true, "Výhra!"));
        } else return ResponseEntity.ok().body(new EndTurnResponse(totalScore, false, null));
    }

    //todo - private broadcastGameState() -> vyzada si z GameService GameState a pomocí messagingTemplate ho pošle do /topic/game-state
    private void broadcastGameState(List<Integer> rolledDice) {
        GameState gameState = gameService.createSnapshotRollDice(rolledDice);
        messagingTemplate.convertAndSend("/topic/game-state", gameState);
    }
}
package com.dice.controller;

import com.dice.dto.*;
import com.dice.service.GameService;
import com.dice.service.RoomsManager;
import com.dice.service.ScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dice")
public class DiceController {

    @Autowired
    private ScoringService scoringService;

    @Autowired
    private GameService gameService;

    @Autowired
    private RoomsManager roomsManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/{roomCode}/roll")
    public ResponseEntity<?> rollDice(@PathVariable String roomCode) {
        int remainingDice = gameService.getActivePlayerRemainingDice(roomCode);

        List<Integer> rolledDice = gameService.rollDice(roomCode, remainingDice);

        broadcastGameState(roomCode, true);

        if (scoringService.isRollScorable(rolledDice)) {
            return ResponseEntity.ok().body(new RollResponse(rolledDice, false, null));
        } else {
            gameService.setActivePlayerRemainingDiceToSix(roomCode);
            gameService.setActivePlayerTurnScoreToZero(roomCode);
            broadcastGameState(roomCode, false);
            return ResponseEntity.ok().body(new RollResponse(rolledDice, true, "Bust! Nic nepadlo. Hraje druhý hráč."));
        }
    }
/*

    @MessageMapping("/game.select-die")
    public void broadcastDieSelection(DieSelectDto dto) {
        // Odesílá se přímo do konkrétní místnosti
        messagingTemplate.convertAndSend("/topic/dice-selection/" + dto.getRoomCode(), dto);
    }

    @PostMapping("/{roomCode}/score")
    public ResponseEntity<?> calculateScore(@PathVariable String roomCode, @RequestBody List<Integer> pickedDice) {
        if (scoringService.hasInvalidDice(pickedDice)) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Neplatné kostky!"));
        }

        int turnScore = scoringService.calculateScore(pickedDice);
        gameService.saveTurnScore(roomCode, turnScore);
        gameService.setActivePlayerRemainingDice(roomCode, pickedDice);
        gameService.removePickedDiceFromTable(roomCode, pickedDice);

        broadcastGameState(roomCode, false);

        List<Integer> diceOnTable = roomsManager.getRoomState(roomCode).getCurrentDiceOnTable();
        return ResponseEntity.ok().body(new TurnStatusResponse(gameService.getTurnScore(roomCode), diceOnTable, null));
    }

    @PostMapping("/{roomCode}/endTurn")
    public ResponseEntity<?> endTurn(@PathVariable String roomCode) {
        int totalScore = gameService.endTurn(roomCode);

        gameService.setActivePlayerRemainingDiceToSix(roomCode);
        gameService.setCurrentDiceOnTableToZero(roomCode);

        broadcastGameState(roomCode, false);

        if (totalScore >= 5000) {
            return ResponseEntity.ok().body(new EndTurnResponse(totalScore, true, "Výhra!"));
        } else {
            return ResponseEntity.ok().body(new EndTurnResponse(totalScore, false, null));
        }
    }
*/

    private void broadcastGameState(String roomCode, boolean isNewRoll) {
        GameState gameState = roomsManager.getRoomState(roomCode);
        gameState.setNewRoll(isNewRoll);
        messagingTemplate.convertAndSend("/topic/game-state/" + roomCode, gameState);
    }
}
package com.dice.controller;

import com.dice.dto.ErrorResponse;
import com.dice.dto.RollResponse;
import com.dice.dto.TurnStatusResponse;
import com.dice.service.GameService;
import com.dice.service.ScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DiceController {

    @Autowired
    private ScoringService scoringService;

    @Autowired
    private GameService gameService;

    @PostMapping("dice/roll")
    public ResponseEntity<?> rollDice() {
      //  List<Integer> rolledDice = scoringService.rollDice(gameService.getActivePlayerRemainingDice());
        List<Integer> rolledDice = new ArrayList<>(List.of(1,2,3,4,5,6));

        if (scoringService.isRollScorable(rolledDice)) {
            return ResponseEntity.ok().body(new RollResponse(rolledDice, false, null));
        }

        else {
            gameService.setActivePlayerRemainingDice(6);
            gameService.setActivePlayerTurnScore(0);
            gameService.switchPlayer();

            return ResponseEntity.ok().body(new RollResponse(rolledDice, true, "Bust! Nic nepadlo. Hraje druhý hráč."));
        }
    }

    @PostMapping("/dice/score")
    public ResponseEntity<?> calculateScore(@RequestBody List<Integer> pickedDice) {
        if (scoringService.isLargeStraight(pickedDice)) {
            gameService.saveTurnScore(3000);
            return ResponseEntity.ok().body(new TurnStatusResponse(gameService.getTurnScore(), null));
        }
        if (scoringService.hasInvalidDice(pickedDice)) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Neplatné kostky!"));
        }

        //todo()
        return null;
    }
}
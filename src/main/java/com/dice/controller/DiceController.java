package com.dice.controller;

import com.dice.dto.ErrorResponse;
import com.dice.service.GameService;
import com.dice.service.ScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DiceController {

    @Autowired
    private ScoringService scoringService;

    @Autowired
    private GameService gameService;

    @PostMapping("dice/roll")
    public List<Integer> rollDice() {
        //  return diceService.rollDice(6);
        return List.of(1, 2, 3, 4, 5, 6);
    }

    @PostMapping("/dice/score")
    public ResponseEntity<?> calculateScore(@RequestBody List<Integer> pickedDice) {
        if (scoringService.hasInvalidDice(pickedDice)) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Neplatné kostky!"));
        }

       if (scoringService.isLargeStraight(pickedDice)) {
           gameService.saveTurnScore();
       }

    }

}
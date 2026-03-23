package com.dice.controller;

import com.dice.dto.TurnStatusResponse;
import com.dice.service.DiceService;
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
    private DiceService diceService;

    @PostMapping("dice/roll")
    public List<Integer> rollDice() {
        //  return diceService.rollDice(6);
        return List.of(1, 2, 3, 4, 5, 6);
    }

    @PostMapping("/dice/score")
    public ResponseEntity<TurnStatusResponse> calculateScore(@RequestBody List<Integer> pickedDice) {
        if (diceService.isLargeStraight(pickedDice)) {
            return ResponseEntity.ok().body(new TurnStatusResponse(3000, 6, null));
        }
        if (diceService.hasInvalidDice(pickedDice)) {
            return ResponseEntity.badRequest().body(new TurnStatusResponse(0, 0, "Neplatné kostky!"));
        } else return ResponseEntity.ok(new TurnStatusResponse(999, 999, null));
    }

}
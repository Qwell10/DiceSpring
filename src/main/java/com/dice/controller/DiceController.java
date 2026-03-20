package com.dice.controller;

import com.dice.service.DiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
        return diceService.rollDice(2);
    }
}

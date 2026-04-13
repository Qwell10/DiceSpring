package com.dice.controller;

import com.dice.dto.GameState;
import com.dice.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DiceWsController {

    @Autowired
    private GameService gameService;

    //todo - finish rollDice()

    @MessageMapping("/roll") // @MessageMapping se stará o to, co jde do serveru od konkrétního hráče
    @SendTo("/topic/dice") // @SendTo vrací pro všechny na tomto endpointu
    public GameState rollDice() {
        return gameService.createSnapshotRollDice();
    }
}

package com.dice.controller;

import com.dice.dto.GameState;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DiceWsController {

    @MessageMapping("/roll") // @MessageMapping se stará o to, co jde do serveru od konkrétního hráče
    @SendTo("/topic/dice") // @SendTo vrací pro všechny na tomto endpointu
    public GameState rollDice() {
        //todo()
        return null;
    }
}

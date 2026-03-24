package com.dice.service;

import com.dice.dto.Player;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    Player player1 = new Player("Jarda", 0, 0, 6);
    Player player2 = new Player("Milan", 0, 0, 6);

    private int activePlayerId = 1;

    public void saveTurnScore() {
        if (activePlayerId == 1) {
            //todo()
        }
    }

}


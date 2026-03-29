package com.dice.service;

import com.dice.dto.Player;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    Player player1 = new Player("Jarda", 0, 0, 6);
    Player player2 = new Player("Milan", 0, 0, 6);

    private int activePlayerId = 1;

    public void switchPlayer() {
        if (activePlayerId == 1) {
            activePlayerId = 2;
        } else activePlayerId = 1;
    }

    public int getActivePlayerRemainingDice() {
        if (activePlayerId == 1) {
            return player1.getRemainingDice();
        } else return player2.getRemainingDice();
    }

    public void setActivePlayerRemainingDice(int diceCount) {
        if (activePlayerId == 1) {
            player1.setRemainingDice(diceCount);
        } else player2.setRemainingDice(diceCount);

    }

    public int getActivePlayerTurnScore() {
        if (activePlayerId == 1) {
            return player1.getTurnScore();
        } else return player2.getTurnScore();
    }

    public void setActivePlayerTurnScore(int turnScore) {
        if (activePlayerId == 1) {
            player1.setTurnScore(turnScore);
        } else player2.setTurnScore(turnScore);
    }


    public void saveTurnScore(int turnScore) {
        if (activePlayerId == 1) {
            //todo()
        }
    }

}


package com.dice.service;

import com.dice.dto.Player;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public int prepareDiceForRoll() {
        if (activePlayerId == 1) {
            if (player1.getRemainingDice() == 0) {
                player1.setRemainingDice(6);
            }
            return player1.getRemainingDice();
        } else {
            if (player2.getRemainingDice() == 0) {
                player2.setRemainingDice(6);
            }
            return player2.getRemainingDice();
        }
    }

    public void setActivePlayerRemainingDiceToSix() {
        if (activePlayerId == 1) {
            player1.setRemainingDice(6);
        } else player2.setRemainingDice(6);

    }

    public void setActivePlayerRemainingDice(List<Integer> pickedDice) {
        if (activePlayerId == 1) {
            player1.setRemainingDice(player1.getRemainingDice() - pickedDice.size());
        } else player2.setRemainingDice(player2.getRemainingDice() - pickedDice.size());
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
            player1.setTurnScore(player1.getTurnScore() + turnScore);
        } else player2.setTurnScore(player2.getTurnScore() + turnScore);
    }

    public int getTurnScore() {
        if (activePlayerId == 1) {
            return player1.getTurnScore();
        } else return player2.getTurnScore();
    }

    public int getTotalScore() {
        if (activePlayerId == 1) {
            return player1.getTotalScore();
        } else return player2.getTotalScore();
    }

    public int endTurn() {
        int totalScore;

        if (activePlayerId == 1) {
            player1.setTotalScore(player1.getTotalScore() + player1.getTurnScore());
            player1.setTurnScore(0);
            player1.setRemainingDice(6);

            totalScore = player1.getTotalScore();
        } else {
            player2.setTotalScore(player2.getTotalScore() + player2.getTurnScore());
            player2.setTurnScore(0);
            player2.setRemainingDice(6);

            totalScore = player2.getTotalScore();
        }

        switchPlayer();

        return totalScore;
    }

}


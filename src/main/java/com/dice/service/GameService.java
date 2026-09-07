package com.dice.service;

import com.dice.dto.GameState;
import com.dice.dto.Player;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GameService {

    @Autowired
    private ScoringService scoringService;

    @Autowired
    private RoomsManager roomsManager;


    @Getter
    private final List<Integer> currentDiceOnTable = new ArrayList<>();


    public List<Integer> rollDice(int amountDice) {
        Random random = new Random();
        ArrayList<Integer> diceNumbers = new ArrayList<>();

        for (int i = 1; i <= amountDice; i++) {
            int number = random.nextInt(6) + 1;
            diceNumbers.add(number);
        }

        currentDiceOnTable.clear();
        currentDiceOnTable.addAll(diceNumbers);

        return diceNumbers;
    }

    public void setCurrentDiceOnTableToZero() {
        currentDiceOnTable.clear();
    }

    public void removePickedDiceFromTable(List<Integer> pickedDice) {
        for (Integer die : pickedDice) {
            currentDiceOnTable.remove(die);
        }
    }

    public void switchPlayer(String roomCode) {
        GameState table = roomsManager.getRoomState(roomCode);
        Player p1 = table.getPlayer1();
        Player p2 = table.getPlayer2();

        if (table.isPlayer1Active()) {
            table.setActivePlayerId(p2.getId());
        } else table.setActivePlayerId(p1.getId());
    }


    //todo - getActivePlayerRemainingDice - metoda nize zakomentovana - tu jsem pouzil v predchozi fazi hry
    public int getActivePlayerRemainingDice(String roomCode) {
        GameState table = roomsManager.getRoomState(roomCode);

        Player activePlayer = table.getActivePlayer();

        if (activePlayer == null) {
            throw new IllegalStateException("Chyba: Aktivní hráč nebyl u stolu nalezen!");
        }

        return activePlayer.getRemainingDice();
    }

/*
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
*/

/*
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

    public GameState createGameStateSnapshot(boolean isNewRoll) {
        return new GameState(player1, player2, currentDiceOnTable, isNewRoll, activePlayerId);
    }
*/

}
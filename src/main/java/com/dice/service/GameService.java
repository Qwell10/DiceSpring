package com.dice.service;

import com.dice.dto.GameState;
import com.dice.dto.Player;
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

    public List<Integer> rollDice(String roomCode, int remainingDice) {
        GameState room = roomsManager.getRoomState(roomCode);
        Random random = new Random();
        List<Integer> diceNumbers = new ArrayList<>();

        for (int i = 1; i <= remainingDice; i++) {
            diceNumbers.add(random.nextInt(6) + 1);
        }

        room.getDiceOnTable().clear();
        room.getDiceOnTable().addAll(diceNumbers);
        return diceNumbers;
    }

    public void setCurrentDiceOnTableToZero(String roomCode) {
        roomsManager.getRoomState(roomCode).getDiceOnTable().clear();
    }

    public void removePickedDiceFromTable(String roomCode, List<Integer> pickedDice) {
        GameState room = roomsManager.getRoomState(roomCode);
        for (Integer die : pickedDice) {
            room.getDiceOnTable().remove(die);
        }
    }

    public int getActivePlayerRemainingDice(String roomCode) {
        return roomsManager.getRoomState(roomCode).getActivePlayer().getRemainingDice();
    }

    public void setActivePlayerRemainingDiceToSix(String roomCode) {
        roomsManager.getRoomState(roomCode).getActivePlayer().setRemainingDice(6);
    }

    public void setActivePlayerTurnScoreToZero(String roomCode) {
        roomsManager.getRoomState(roomCode).getActivePlayer().setTurnScore(0);
    }

    public void setActivePlayerRemainingDice(String roomCode, List<Integer> pickedDice) {
        Player activePlayer = roomsManager.getRoomState(roomCode).getActivePlayer();
        activePlayer.setRemainingDice(activePlayer.getRemainingDice() - pickedDice.size());
    }

    public void saveTurnScore(String roomCode, int turnScore) {
        Player activePlayer = roomsManager.getRoomState(roomCode).getActivePlayer();
        activePlayer.setTurnScore(activePlayer.getTurnScore() + turnScore);
    }

    public int getTurnScore(String roomCode) {
        return roomsManager.getRoomState(roomCode).getActivePlayer().getTurnScore();
    }

/*
    public int endTurn(String roomCode) {
        GameState room = roomsManager.getRoomState(roomCode);
        Player activePlayer = room.getActivePlayer();

        activePlayer.setTotalScore(activePlayer.getTotalScore() + activePlayer.getTurnScore());
        activePlayer.setTurnScore(0);
        activePlayer.setRemainingDice(6);

        int totalScore = activePlayer.getTotalScore();
        switchPlayer(roomCode);
        return totalScore;
    }
*/

    /*
    public void switchPlayer(String roomCode) {
        GameState room = roomsManager.getRoomState(roomCode);
        Player p1 = room.getPlayer1();
        Player p2 = room.getPlayer2();

        if (room.getActivePlayerId() == 1) {
            room.setActivePlayerId(2);
        } else {
            room.setActivePlayerId(1);
        }
    }
*/

}
package com.dice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameState {

    private Player player1;
    private Player player2;
    private List<Integer> diceOnTable;
    private String activePlayerId;
    private boolean isNewRoll;

    public boolean isPlayer1Connected() {
        return player1 != null && player1.isConnected();
    }

    public boolean isPlayer2Connected() {
        return player2 != null && player2.isConnected();
    }

    public Player getActivePlayer() {
        if (activePlayerId.equals(player1.getId())) {
            return player1;
        } else return player2;
    }
}
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
    private boolean isNewRoll;
    private String activePlayerId;

    public boolean isPlayer1Active() {
        return activePlayerId != null && player1 != null && activePlayerId.equals(player1.getId());
    }

    public boolean isPlayer2Active() {
        return activePlayerId != null && player2 != null && activePlayerId.equals(player2.getId());
    }

    public Player getActivePlayer() {
        if (activePlayerId.equals(player1.getId())) {
            return player1;
        } else return player2;
    }
}
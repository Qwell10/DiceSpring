package com.dice.dto;

import java.util.List;

public record GameState(
        Player player1,
        Player player2,
    //    boolean isPlayer1Connected,
     //   boolean isPlayer2Connected,
        List<Integer> rolledDice,
        int activePlayerId
) {}

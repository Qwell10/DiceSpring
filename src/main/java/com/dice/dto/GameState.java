package com.dice.dto;

import java.util.List;

public record GameState(
        Player player1,
        Player player2,
        List<Integer> rolledDice,
        int activePlayerId
) {}

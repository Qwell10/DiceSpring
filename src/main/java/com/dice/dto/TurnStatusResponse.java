package com.dice.dto;

import java.util.List;

public record TurnStatusResponse(
        int turnScore,
        List<Integer> diceOnTable,
        String message
) {}
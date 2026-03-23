package com.dice.dto;

public record TurnStatusResponse(
        int score,
        int remainingDice,
        String errorMessage
) {}
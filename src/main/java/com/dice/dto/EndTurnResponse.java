package com.dice.dto;

public record EndTurnResponse(
        int totalScore,
        boolean isWinner,
        String message
) {}

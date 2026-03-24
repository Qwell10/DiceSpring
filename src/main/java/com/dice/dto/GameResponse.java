package com.dice.dto;

public record GameResponse(
        int activePlayerId,
        int player1TotalScore,
        int player2TotalScore,
        int currentTurnScore,
        int remainingDice,
        String errorMessage
) {}

package com.dice.dto;

public record EndTurnResponse(
        int totalScore
        //boolean isWinner, String message  -> todo later when player win
) {}

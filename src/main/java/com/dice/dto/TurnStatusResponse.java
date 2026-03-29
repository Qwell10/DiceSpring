package com.dice.dto;

public record TurnStatusResponse(
        int turnScore,
        String message
) {}
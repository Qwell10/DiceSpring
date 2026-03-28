package com.dice.dto;

import java.util.List;

public record RollResponse(
        List<Integer> dice,
        boolean isBust,
        String message
) {}

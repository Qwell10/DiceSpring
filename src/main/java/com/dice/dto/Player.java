package com.dice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Player {
    String name;
    int totalScore;
    int turnScore;
    int remainingDice;
}

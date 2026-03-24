package com.dice.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ScoringService {

    public List<Integer> rollDice(int amountDice) {
        Random random = new Random();
        ArrayList<Integer> diceNumbers = new ArrayList<>();

        for (int i = 1; i <= amountDice; i++) {
            int number = random.nextInt(amountDice) + 1;
            diceNumbers.add(number);
        }
        return diceNumbers;
    }

    public boolean hasInvalidDice(List<Integer> pickedDice) {
        int[] diceCounts = getDiceCounts(pickedDice);

        for (int i = 2; i < diceCounts.length; i++) {
            if (i == 5) {
                continue;
            }
            if (diceCounts[i] != 0 && diceCounts[i] < 3) {
                return true;
            }
        }
        return false;
    }

    public boolean isLargeStraight(List<Integer> pickedDice) {
        int[] diceCounts = getDiceCounts(pickedDice);

        int marker = 0;

        for (int die : diceCounts) {
            if (die == 1) {
                marker++;
            }
        }

        return marker == 6;
    }


    private int[] getDiceCounts(List<Integer> pickedDice) {
        int[] diceCounts = new int[7];

        for (int die : pickedDice) {
            diceCounts[die]++;
        }

        return diceCounts;
    }

}

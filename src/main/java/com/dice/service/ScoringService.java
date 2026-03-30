package com.dice.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class ScoringService {

    public List<Integer> rollDice(int amountDice) {
        Random random = new Random();
        ArrayList<Integer> diceNumbers = new ArrayList<>();

        for (int i = 1; i <= amountDice; i++) {
            int number = random.nextInt(6) + 1;
            diceNumbers.add(number);
        }
        return diceNumbers;
    }

    public boolean isRollScorable(List<Integer> rolledDice) {
        if (rolledDice.contains(1) || rolledDice.contains(5)) {
            return true;
        }

        int[] diceCounts = getDiceCounts(rolledDice);

        for (int count : diceCounts) {
            if (count >= 3) {
                return true;
            }
        }
        return false;
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

    public boolean isSmallStraight(List<Integer> pickedDice) {
        int[] diceCounts = getDiceCounts(pickedDice);

        return (diceCounts[1] >= 1 && diceCounts[2] == 1 && diceCounts[3] == 1 && diceCounts[4] == 1 && diceCounts[5] >= 1) ||
                (diceCounts[2] == 1 && diceCounts[3] == 1 && diceCounts[4] == 1 && diceCounts[5] >= 1 && diceCounts[6] == 1);
    }

    public boolean containsOnes(List<Integer> pickedDice) {
        int onesCount = Collections.frequency(pickedDice, 1);

        return onesCount > 1;
    }

    public boolean containsFives(List<Integer> pickedDice) {
        int fivesCount = Collections.frequency(pickedDice, 5);

        return fivesCount > 1;
    }

    public int calculateScore(List<Integer> pickedDice) {
        int[] diceCounts = getDiceCounts(pickedDice);

        int score = 0;

        score += calculateOnes(diceCounts);
        score += calculateFives(diceCounts);
        score += calculateStandardDice(diceCounts, 2);
        score += calculateStandardDice(diceCounts, 3);
        score += calculateStandardDice(diceCounts, 4);
        score += calculateStandardDice(diceCounts, 6);

        return score;
    }

    private int calculateStandardDice(int[] diceCounts, int dieValue) {
        if (diceCounts[dieValue] == 0) {
            return 0;
        } else if (diceCounts[dieValue] == 3) {
            return (dieValue * 100);
        } else if (diceCounts[dieValue] == 4) {
            return (dieValue * 100) * 2;
        } else if (diceCounts[dieValue] == 5) {
            return (dieValue * 100) * 4;
        } else return (dieValue * 100) * 8;
    }

    private int calculateOnes(int[] diceCounts) {
        if (diceCounts[1] == 0) {
            return 0;
        } else if (diceCounts[1] == 1) {
            return 100;
        } else if (diceCounts[1] == 2) {
            return 200;
        } else if (diceCounts[1] == 3) {
            return 1000;
        } else if (diceCounts[1] == 4) {
            return 2000;
        } else if (diceCounts[1] == 5) {
            return 4000;
        } else return 8000;
    }

    private int calculateFives(int[] diceCounts) {
        if (diceCounts[5] == 0) {
            return 0;
        } else if (diceCounts[5] == 1) {
            return 50;
        } else if (diceCounts[5] == 2) {
            return 100;
        } else if (diceCounts[5] == 3) {
            return 500;
        } else if (diceCounts[5] == 4) {
            return 1000;
        } else if (diceCounts[5] == 5) {
            return 2000;
        } else return 4000;
    }

    private int[] getDiceCounts(List<Integer> pickedDice) {
        int[] diceCounts = new int[7];

        for (int die : pickedDice) {
            diceCounts[die]++;
        }

        return diceCounts;
    }

}

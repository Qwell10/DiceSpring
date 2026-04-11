package com.dice.service;

import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private boolean isPlayer1Occupied = false;
    private boolean isPlayer2Occupied = false;

    public int assignId() {
        if (!isPlayer1Occupied) {
            isPlayer1Occupied = true;
            return 1;

        } else if (!isPlayer2Occupied) {
            isPlayer2Occupied = true;
            return 2;
        } else return 0;
    }

    public String assignRole(int id) {
        return (id == 0) ? "SPECTATOR" : "PLAYER";
    }

}

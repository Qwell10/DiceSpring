package com.dice.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RegistrationService {

    private final Map<String, Integer> sessionToPlayerMap = new ConcurrentHashMap<>();

    private boolean isPlayer1Connected = false;
    private boolean isPlayer2Connected = false;

    public int assignId() {
        if (!isPlayer1Connected) {
            isPlayer1Connected = true;
            return 1;

        } else if (!isPlayer2Connected) {
            isPlayer2Connected = true;
            return 2;
        } else return 0;
    }

    public String assignRole(int id) {
        return (id == 0) ? "SPECTATOR" : "PLAYER";
    }

}

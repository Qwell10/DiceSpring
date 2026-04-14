package com.dice.service;

import com.dice.dto.PlayerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RegistrationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Map<String, Integer> sessionToPlayerMap = new ConcurrentHashMap<>();

    private boolean isPlayer1Connected = false;
    private boolean isPlayer2Connected = false;

    // SYNCHRONIZED zajistí to, že do metody může vstoupit jen jedno vlákno
    // Nestane se to, že by se ve stejnou chvíli připojili 2 hráči a oboum se přiřadí id 1
    public synchronized int assignId() {
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

    public void registerSession(String sessionId, int playerId) {
        sessionToPlayerMap.put(sessionId, playerId);
        broadcastStatus();
    }

    public void unregisterSession(String sessionId) {
        Integer playerId = sessionToPlayerMap.remove(sessionId);
        if (playerId == null) {
            return;
        }
        if (playerId == 1) isPlayer1Connected = false;
        if (playerId == 2) isPlayer2Connected = false;

        broadcastStatus();
    }

    public void broadcastStatus() {
        PlayerStatus playerStatus = new PlayerStatus(isPlayer1Connected, isPlayer2Connected);
        messagingTemplate.convertAndSend("/topic/player-status", playerStatus);
    }
}

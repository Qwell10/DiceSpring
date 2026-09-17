package com.dice.service;

import com.dice.dto.GameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//todo - nebude potreba kvuli RoomsController

@Service
public class RegistrationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RoomsManager roomsManager;

    private final Map<String, SessionData> sessionToPlayerMap = new ConcurrentHashMap<>();

    public void registerSession(String sessionId, String playerId, String roomCode) {
        sessionToPlayerMap.put(sessionId, new SessionData(playerId, roomCode));
        System.out.println("Připojen hráč " + playerId + " do místnosti " + roomCode);
    }

    public void unregisterSession(String sessionId) {
        SessionData data = sessionToPlayerMap.remove(sessionId);

        if (data == null) {
            return;
        }

        String disconnectedPlayerId = data.playerId;
        String roomCode = data.roomCode;

        System.out.println("Odpojil se hráč " + disconnectedPlayerId + " z místnosti " + roomCode);

        roomsManager.playerDisconnected(roomCode, disconnectedPlayerId);
    }

    //todo
    private void broadcastRoomStatus(String roomCode) {
        GameState gameState = roomsManager.getRoomState(roomCode);

        Boolean isPlayer1Active = gameState.isPlayer1Active();
        Boolean isPlayer2Active = gameState.isPlayer2Active();

        Map<String, Boolean> activePlayers = new HashMap<>();
        activePlayers.put("isPlayer1Active", isPlayer1Active);
        activePlayers.put("isPlayer2Active", isPlayer2Active);

        messagingTemplate.convertAndSend();


    }

    static class SessionData {
        String playerId;
        String roomCode;

        public SessionData(String playerId, String roomCode) {
            this.playerId = playerId;
            this.roomCode = roomCode;
        }
    }

}

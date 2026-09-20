package com.dice.service;

import com.dice.dto.GameState;
import com.dice.dto.Player;
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

        GameState gameState = roomsManager.getRoomState(roomCode);

        if (gameState == null) {
            return;
        }

        Player p1 = gameState.getPlayer1();
        Player p2 = gameState.getPlayer2();

        if (p1 != null && p1.getId().equals(playerId)) {
            p1.setConnected(true);
        } else if (p2 != null && p2.getId().equals(playerId)) {
            p2.setConnected(true);
        }

        broadcastRoomStatus(roomCode);
    }

    //todo
    public void unregisterSession(String sessionId) {
        SessionData data = sessionToPlayerMap.remove(sessionId);

        if (data == null) {
            return;
        }

        String disconnectedPlayerId = data.playerId;
        String roomCode = data.roomCode;

        System.out.println("Odpojil se hráč " + disconnectedPlayerId + " z místnosti " + roomCode);

        GameState gameState = roomsManager.getRoomState(roomCode);

        if (gameState == null) {
            return;
        }



        roomsManager.playerDisconnected(roomCode, disconnectedPlayerId);
    }

    public void broadcastRoomStatus(String roomCode) {
        GameState gameState = roomsManager.getRoomState(roomCode);
        if (gameState == null) {
            return;
        }

        Boolean isPlayer1Connected = gameState.isPlayer1Connected();
        Boolean isPlayer2Connected = gameState.isPlayer2Connected();

        Map<String, Boolean> activePlayers = new HashMap<>();
        activePlayers.put("isPlayer1Connected", isPlayer1Connected);
        activePlayers.put("isPlayer2Connected", isPlayer2Connected);

        messagingTemplate.convertAndSend("/topic/player-status/" + roomCode, activePlayers);
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

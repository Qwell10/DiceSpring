package com.dice.service;

import com.dice.dto.PlayerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//todo - nebude potreba kvuli RoomsController

@Service
public class RegistrationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Map<String, SessionData> sessionToPlayerMap = new ConcurrentHashMap<>();

    public void registerSession(String sessionId, String playerId, String roomCode) {
        sessionToPlayerMap.put(sessionId, new SessionData(playerId, roomCode));
        System.out.println("Připojen hráč " + playerId + " do místnosti " + roomCode);
    }

    //todo 1 - zmenit (pridat odstraneni z konkretniho stolu - podobne jako registerSession)
    public void unregisterSession(String sessionId) {
        SessionData data = sessionToPlayerMap.remove(sessionId);

        if (data == null) {
            return;
        }

        String disconnectedPlayerId = data.playerId;
        String roomCode = data.roomCode;

        System.out.println("Odpojil se hráč " + disconnectedPlayerId + " z místnosti " + roomCode);
    }

    //todo 2 - zmena - bude aktualni pro konkretni stoly
    public void broadcastStatus() {
        // Takto to vypadalo pro globalni jeden stul (zadny jiny neexistoval)
      //  PlayerStatus playerStatus = new PlayerStatus(isPlayer1Connected, isPlayer2Connected);
      //  messagingTemplate.convertAndSend("/topic/player-status", playerStatus);
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

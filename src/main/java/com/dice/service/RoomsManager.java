package com.dice.service;

import com.dice.dto.GameState;
import com.dice.dto.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomsManager {

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int ROOM_CODE_LENGTH = 5;
    private final Random random = new Random();

    private final Map<String, GameState> activeRooms = new ConcurrentHashMap<>();

    public String createNewRoom(Player player1) {
        String roomCode = generateRoomCode();

        GameState startingGameState = new GameState(
                player1,
                null,
                new ArrayList<>(),
                player1.getId(),
                true
        );

        activeRooms.put(roomCode, startingGameState);
        return roomCode;
    }

    public GameState getRoomState(String roomCode) {
        return activeRooms.get(roomCode);
    }


    private String generateRoomCode() {
        String newCode;

        do {
            StringBuilder roomCodeBuilder = new StringBuilder();
            for (int i = 0; i < ROOM_CODE_LENGTH; i++) {
                int randomIndex = random.nextInt(CHARS.length());
                roomCodeBuilder.append(CHARS.charAt(randomIndex));
            }
            newCode = roomCodeBuilder.toString();

        } while (activeRooms.containsKey(newCode));

        return newCode;
    }

    //todo
    public void playerDisconnected(String roomCode, String disconnectedPlayerId) {
        GameState room = getRoomState(roomCode);

    }
}

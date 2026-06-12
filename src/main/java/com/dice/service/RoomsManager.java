package com.dice.service;

import com.dice.dto.GameState;
import com.dice.dto.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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
                true,
                player1.getId()
        );

        activeRooms.put(roomCode, startingGameState);
        return roomCode;
    }

    public void updateRoomState(String roomCode, GameState newGameState) {
        activeRooms.put(roomCode, newGameState);
    }

    public GameState getRoomState(String roomCode) {
        return activeRooms.get(roomCode);
    }


    private String generateRoomCode() {
        StringBuilder codeBuilder;
        String newCode;

        do {
            codeBuilder = new StringBuilder();
            for (int i = 0; i < ROOM_CODE_LENGTH; i++) {
                int randomIndex = random.nextInt(CHARS.length());
                codeBuilder.append(CHARS.charAt(randomIndex));
            }
            newCode = codeBuilder.toString();

        } while (activeRooms.containsKey(newCode));

        return newCode;
    }
}

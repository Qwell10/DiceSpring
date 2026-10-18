package com.dice.service;

import com.dice.dto.GameState;
import com.dice.dto.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomsManager {
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

    //todo - neni 100%, potreba doladit -- UUID je ulozene do sessionStorage na FE
    public String joinRoom(String roomCode, String playerId) {
        GameState room = getRoomState(roomCode);

        if (room == null) {
            throw new IllegalArgumentException("Místnost s tímto kódem neexistuje.");
        }

        if (playerId == null) {
            String newPlayerId = UUID.randomUUID().toString();
            Player newPlayer = new Player(newPlayerId, "Milouš", 0, 0, 6, false);
            room.setPlayer2(newPlayer);

            return newPlayerId;
        }

        if (room.getPlayer1() != null && playerId.equals(room.getPlayer1().getId())) {
            return playerId;
        }

        if (room.getPlayer2() != null && playerId.equals(room.getPlayer2().getId())) {
            return playerId;
        }

        throw new IllegalArgumentException("Neplatné ID hráče nebo je místnost plná.");
    }


    public void playerConnected(String roomCode, String connectedPlayerId) {
        GameState room = getRoomState(roomCode);

        if (room == null) {
            return;
        }

        Player p1 = room.getPlayer1();
        Player p2 = room.getPlayer2();

        if (p1 != null && p1.getId().equals(connectedPlayerId)) {
            p1.setConnected(true);
        } else if (p2 != null && p2.getId().equals(connectedPlayerId)) {
            p2.setConnected(true);
        }
    }

    public void playerDisconnected(String roomCode, String disconnectedPlayerId) {
        GameState room = getRoomState(roomCode);

        if (room == null) {
            return;
        }

        Player p1 = room.getPlayer1();
        Player p2 = room.getPlayer2();

        if (p1 != null && p1.getId().equals(disconnectedPlayerId)) {
            p1.setConnected(false);
        } else if (p2 != null && p2.getId().equals(disconnectedPlayerId)) {
            p2.setConnected(false);
        }
    }

    public GameState getRoomState(String roomCode) {
        return activeRooms.get(roomCode);
    }

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int ROOM_CODE_LENGTH = 5;
    private final Random random = new Random();

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
}

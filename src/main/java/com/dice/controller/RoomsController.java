package com.dice.controller;

import com.dice.dto.Player;
import com.dice.dto.RoomResponse;
import com.dice.service.RoomsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dice")
public class RoomsController {

    @Autowired
    private RoomsManager roomsManager;

    @PostMapping("/create-room")
    public ResponseEntity<RoomResponse> createRoom() {
        String playerId = UUID.randomUUID().toString();

        Player player1 = new Player(playerId, "Player1", 0, 0, 6);

        String roomCode = roomsManager.createNewRoom(player1);

        return ResponseEntity.ok().body(new RoomResponse(roomCode, playerId));

    }

    //todo - join room controller
}

package com.dice.controller;

import com.dice.dto.ErrorResponse;
import com.dice.dto.GameState;
import com.dice.dto.Player;
import com.dice.dto.RoomResponse;
import com.dice.service.RegistrationService;
import com.dice.service.RoomsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dice")
public class RoomsController {

    @Autowired
    private RoomsManager roomsManager;

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/create-room")
    public ResponseEntity<RoomResponse> createRoom() {
        String player1Id = UUID.randomUUID().toString();

        Player player1 = new Player(player1Id, "Player1", 0, 0, 6);

        String roomCode = roomsManager.createNewRoom(player1);

        return ResponseEntity.ok().body(new RoomResponse(roomCode, player1Id));
    }

    @PostMapping("/join-room/{roomCode}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomCode) {
        GameState table = roomsManager.getRoomState(roomCode);
        if (table == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Místnost s tímto kódem neexistuje."));
        }

        if (table.getPlayer2() != null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Místnost je již plná."));
        }

        String player2Id = UUID.randomUUID().toString();
        Player player2 = new Player(player2Id, "Player2", 0, 0, 6);

        table.setPlayer2(player2);

        return ResponseEntity.ok().body(new RoomResponse(roomCode, player2Id));
    }

    @MessageMapping("/status/request/{roomCode}")
    public void requestStatus(@DestinationVariable String roomCode) {
        registrationService.broadcastRoomStatus(roomCode);
    }
}

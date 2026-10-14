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

        Player player1 = new Player(player1Id, "Player1", 0, 0, 6, false);

        String roomCode = roomsManager.createNewRoom(player1);

        return ResponseEntity.ok().body(new RoomResponse(roomCode, player1Id));
    }

                             //todo 1 - presunuti business logiky do RoomsManager
                             //todo 2 - odchod hrace - znovu napojeni - "mistnost je plna"
    @PostMapping("/join-room/{roomCode}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomCode, @RequestParam(required = false) String playerId) {
        try {
            String finalPlayerId = roomsManager.joinRoom(roomCode, playerId);

            return ResponseEntity.ok().body(new RoomResponse(roomCode, finalPlayerId));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @MessageMapping("/status/request/{roomCode}")
    public void requestStatus(@DestinationVariable String roomCode) {
        registrationService.broadcastPlayerStatus(roomCode);
    }
}

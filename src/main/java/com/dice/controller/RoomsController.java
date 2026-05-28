package com.dice.controller;

import com.dice.dto.RoomResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dice")
public class RoomsController {

    @PostMapping("/create-room")
    public RoomResponse createRoom() {
        //todo()
        return null;
    }
}

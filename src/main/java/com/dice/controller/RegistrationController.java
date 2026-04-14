package com.dice.controller;

import com.dice.dto.PlayerStatus;
import com.dice.dto.RegistrationResponse;
import com.dice.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dice")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/join")
    public RegistrationResponse joinGame() {
        int id = registrationService.assignId();
        String role = registrationService.assignRole(id);

        return new RegistrationResponse(id, role);
    }

    @GetMapping("/status")
    public PlayerStatus getCurrentStatus() {
        return new PlayerStatus(
                registrationService.isPlayer1Connected(),
                registrationService.isPlayer2Connected()
        );
    }

}

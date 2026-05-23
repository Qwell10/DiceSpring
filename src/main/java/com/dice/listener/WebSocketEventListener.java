package com.dice.listener;

import com.dice.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private RegistrationService registrationService;

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String playerIdStr = accessor.getFirstNativeHeader("playerId");
        String sessionId = accessor.getSessionId();

        if (playerIdStr != null) {
            int playerId = Integer.parseInt(playerIdStr);

            registrationService.registerSession(sessionId, playerId);

            System.out.println("✅ Connected: Session " + sessionId + " -> Player " + playerId);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();

        registrationService.unregisterSession(sessionId);

        System.out.println("❌ Disconnected: Session " + sessionId);
    }

}

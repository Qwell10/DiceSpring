package com.dice.listener;

import com.dice.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private RegistrationService registrationService;

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String playerId = accessor.getFirstNativeHeader("playerId");
        String sessionId = accessor.getSessionId();


    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        //todo(((((((((((((((())))))))))))))))) - after handleConnect()
    }

}

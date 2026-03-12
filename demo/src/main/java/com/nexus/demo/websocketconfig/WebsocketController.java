package com.nexus.demo.websocketconfig;

import com.nexus.demo.WebSocketSessionManager;
import com.nexus.demo.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    @Autowired
    public WebsocketController(SimpMessagingTemplate messagingTemplate, WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/messages")
    public void handleMessage(Message message) {
        System.out.println("Received message from user: " + message.getUser() + ": " + message.getMessage());
        messagingTemplate.convertAndSend("/topic/messages", message); // (Server) Broadcast user message to all the users
        System.out.println("Sent message to /topic/messages: " + message.getUser() + ": " + message.getMessage());
    }

    @MessageMapping("/connect")
    public void connectUser(String username) {
        sessionManager.addUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(username + " connected");
    }

    @MessageMapping("/disconnect")
    public void disconnectUser(String username) {
        sessionManager.removeUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(username + " disconnected");
    }

    @MessageMapping("/request-users")
    public void requestUsers(){
        sessionManager.broadcastActiveUsernames();
        System.out.println("Requesting Users");
    }
}

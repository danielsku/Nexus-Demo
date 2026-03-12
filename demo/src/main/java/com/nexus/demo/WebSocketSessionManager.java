package com.nexus.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class WebSocketSessionManager {
    private final ArrayList<String> activeUsernames = new ArrayList<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketSessionManager(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate; // NOTE. Spring uses singleton, so this is the same Messaging template as in the controller class
    }

    public void addUsername(String username) {
        System.out.println("addUsername called: " + username);
        activeUsernames.add(username);
    }

    public void removeUsername(String username) {
        System.out.println("removeUsername called: " + username);
        activeUsernames.remove(username);
    }

    public void broadcastActiveUsernames() {
        messagingTemplate.convertAndSend("/topic/users", activeUsernames);
        System.out.println("Broadcasting active users to /topic/users" + activeUsernames);
    }
}

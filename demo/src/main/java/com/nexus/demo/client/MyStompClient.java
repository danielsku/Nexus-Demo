package com.nexus.demo.client;

import com.nexus.demo.pojo.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyStompClient {

    private StompSession session;
    // Allows us to connect to STOMP servers
    // Has methods that allows 1. Send messages; 2. Subscribe to Routes; 3. Manage the Connection

    private String username;
    private MessageListener messageListener;

    public MyStompClient(MessageListener messageListener, String username) throws ExecutionException, InterruptedException {
        this.messageListener = messageListener;
        this.username = username;

        List<Transport> transports = new ArrayList<>();
        // Transport - web communication - method or protocol used to transfer data between client <-> server

        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        // Allows SockJS ~ to communicate w/ websocket server

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        // SockJS can have issues w/ websockets, so we need WebSocketStompClient here
        // Now SockJs can use STOMP protocols

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        // Serialize/Deserialize information to be able to send to/ receive from websockets

        StompSessionHandler sessionHandler = new MyStompSessionHandler(messageListener, username);
        // Instantiate the StompSessionHandler (pass in the username)

        String url = "ws://localhost:8080/ws"; // Use ws:// for WebSocket
        // url to connect to our websocket

        session = stompClient.connectAsync(url, sessionHandler).get();
    }

    public void sendMessage(Message message) {
        try {
            session.send("/app/messages", message);
            System.out.println("Message Sent: " + message.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectUser(String username) {
        session.send("/app/disconnect", username);
        System.out.println("Disconnect User: " + username);
    }
}

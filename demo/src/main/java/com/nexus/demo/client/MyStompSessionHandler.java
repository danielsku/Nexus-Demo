package com.nexus.demo.client;

import com.nexus.demo.pojo.Message;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private String username;
    private MessageListener messageListener;

    public MyStompSessionHandler(MessageListener messageListener, String username) {
        this.username = username;
        this.messageListener = messageListener;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Client Connected");
        session.send("/app/connect", username);
        session.subscribe("/topic/messages", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }
            // Informs client of the expected payload type ("Message" class)

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {

                try {
                    if (payload instanceof Message) {
                        Message message = (Message) payload;
                        messageListener.onMessageReceive(message);
                        System.out.println("Received message: " + message.getUser() + ": " + message.getMessage());
                    } else {
                        System.out.println("Received unexpected payload type: " + payload.getClass());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Checks if of Message type, stores it as Message type if so

        });
        System.out.println("Client Subscribe to /topic/messages");
        // The two overwritten methods handle the payload received from the subscribed destination "/topic/messages"

        session.subscribe("/topic/users", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return new ArrayList<String>().getClass();
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try{
                    if(payload instanceof ArrayList){
                        ArrayList<String> activeUsers = (ArrayList<String>) payload;
                        messageListener.onActiveUsersUpdated(activeUsers);
                        System.out.println("Received active users: " + activeUsers);
                    } else {
                        System.out.println("Received unexpected payload type: " + payload.getClass());
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Client Subscribe to /topic/users");

        session.send("/app/request-users", "");
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        exception.printStackTrace();
    }

}

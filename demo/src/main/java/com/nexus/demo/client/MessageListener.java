package com.nexus.demo.client;

import com.nexus.demo.pojo.Message;

import java.util.ArrayList;

public interface MessageListener {
    void onMessageReceive(Message message);
    void onActiveUsersUpdated(ArrayList<String> users);
}

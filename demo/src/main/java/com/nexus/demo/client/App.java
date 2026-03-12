package com.nexus.demo.client;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args){
        // UIManager.put("defaultFont", new Font("Arial", Font.PLAIN, 14));
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            ClientGUI frame = null;
            try {
                frame = new ClientGUI("NexusUser");
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            frame.setVisible(true);
        });
    }
}

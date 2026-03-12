package com.nexus.demo.client;

import com.nexus.demo.pojo.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ClientGUI extends JFrame implements MessageListener {

    private JPanel connectedUsersPanel;
    private JPanel messageFeedPanel;
    private JPanel messagePanel;

    // private JPanel chatListPanel; // added later for groupchats

    private MyStompClient myStompClient;
    private String username;

    private Message receivedMessage;


    public ClientGUI(String username) throws ExecutionException, InterruptedException {
        super("Nexus Chat Messenger: " + username);
        this.username = username;
        myStompClient = new MyStompClient(this, username);

        setSize(1218, 685);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        ClientGUI.this,
                        "Are you sure?",
                        "Exit",
                        JOptionPane.YES_NO_OPTION
                );

                if(option == JOptionPane.YES_OPTION){
                    myStompClient.disconnectUser(username);
                    dispose(); // Could do it explicitly w/ ClientGUI.this.dispose();
                }
            }
        });

        getContentPane().setBackground(Utilities.PRIMARY_COLOR);
        addGuiComponents();

    }

    private void addGuiComponents() {
        addConnectedUsersComponent();
        addMessageFeed();
        addChatList();
    }

    private void addChatList() {

    }

    private void addMessageFeed() {
        messageFeedPanel = new JPanel();
        messageFeedPanel.setLayout(new BorderLayout());
        messageFeedPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Utilities.TRANSPARENT_COLOR);
        messageFeedPanel.add(messagePanel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField inputField = new JTextField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if((e.getKeyChar() == KeyEvent.VK_ENTER) && (!inputField.getText().isEmpty())){
                    String input = inputField.getText();

                    inputField.setText("");

                    myStompClient.sendMessage(new Message(username, input));

                }
            }
        });
        inputField.setBackground(Utilities.SECONDARY_COLOR);
        inputField.setForeground(Utilities.TEXT_COLOR);
        inputField.setFont(new Font("Arial", Font.PLAIN, 16 ));
        inputField.setPreferredSize(new Dimension(inputPanel.getWidth(), 50));
        inputPanel.add(inputField, BorderLayout.CENTER);
        messageFeedPanel.add(inputPanel, BorderLayout.SOUTH);;

        add(messageFeedPanel, BorderLayout.CENTER);
    }

    private void addConnectedUsersComponent() {
        connectedUsersPanel = new JPanel();
        connectedUsersPanel.setLayout(new BoxLayout(connectedUsersPanel, BoxLayout.Y_AXIS));
        connectedUsersPanel.setBackground(Utilities.SECONDARY_COLOR);
        connectedUsersPanel.setPreferredSize(new Dimension(200, getHeight()));

        JLabel connectedUsersLabel = new JLabel("Connected Users");
        connectedUsersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectedUsersLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        connectedUsersLabel.setFont(new Font("Lato", Font.PLAIN, 20));
        connectedUsersLabel.setForeground(Utilities.TEXT_COLOR);
        connectedUsersPanel.add(connectedUsersLabel);

        add(connectedUsersPanel, BorderLayout.EAST);
    }

    private JPanel createChatMessage(Message message){
        JPanel chatMessage = new JPanel();
        chatMessage.setLayout(new BoxLayout(chatMessage, BoxLayout.Y_AXIS));
        chatMessage.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        chatMessage.setBackground(Utilities.TRANSPARENT_COLOR);

        JLabel usernameLabel = new JLabel(message.getUser());
        usernameLabel.setFont(new Font("Lato", Font.BOLD, 18));
        usernameLabel.setForeground((Utilities.TEXT_COLOR));
        chatMessage.add(usernameLabel);

        JLabel messageBody = new JLabel(message.getMessage());
        messageBody.setFont(new Font("Lato", Font.PLAIN, 18));
        messageBody.setForeground((Utilities.TEXT_COLOR));
        chatMessage.add(messageBody);

        return chatMessage;
    }

    @Override
    public void onMessageReceive(Message message) {
        messagePanel.add(createChatMessage(message));
        revalidate();
        repaint();
    }

    @Override
    public void onActiveUsersUpdated(ArrayList<String> users) {

        // More efficient way to implement this could be
        // JLabels in a Hashset (Since every user is unique)
        // Access the JLabel to be deleted (the user that was disconnected)
        // .remove(JLabel), revalidate(), repaint()

        if(connectedUsersPanel.getComponents().length >= 2) {
            connectedUsersPanel.remove(1);
        }

        JPanel userListPanel = new JPanel();
        userListPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));

        for(String user : users) {
            JLabel username = new JLabel();
            username.setText(user);
            username.setForeground(Utilities.TEXT_COLOR);
            username.setFont(new Font("Inter", Font.BOLD, 16));
            userListPanel.add(username);
        }

        connectedUsersPanel.add(userListPanel);
        revalidate();
        repaint();
    }
}

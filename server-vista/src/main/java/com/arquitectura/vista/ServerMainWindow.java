package com.arquitectura.vista;

import com.arquitectura.controlador.ServerViewController;
import com.arquitectura.domain.Channel;
import com.arquitectura.domain.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ServerMainWindow extends JFrame {

    private final ServerViewController controller;
    private JTextArea displayArea;
    private JButton refreshUsersButton;
    private JButton refreshChannelsButton;

    public ServerMainWindow(ServerViewController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        // Basic window setup
        setTitle("Admin Panel - Chat Server");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Display area for information
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        refreshUsersButton = new JButton("Refresh User List");
        refreshChannelsButton = new JButton("Refresh Channel List");
        buttonPanel.add(refreshUsersButton);
        buttonPanel.add(refreshChannelsButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add event listeners for buttons
        addListeners();
    }

    private void addListeners() {
        refreshUsersButton.addActionListener(e -> {
            // When the button is clicked, call the controller
            List<User> users = controller.obtenerUsuariosRegistrados();
            // The controller returns the data, and we update the view
            updateUserDisplay(users);
        });

        refreshChannelsButton.addActionListener(e -> {
            List<Channel> channels = controller.obtenerCanalesActivos();
            updateChannelDisplay(channels);
        });
    }

    public void display() {
        setVisible(true);
    }

    private void updateUserDisplay(List<User> users) {
        displayArea.setText("--- REGISTERED USERS ---\n");
        for (User user : users) {
            displayArea.append("ID: " + user.getId() + ", Username: " + user.getUsername() + ", Email: " + user.getEmail() + "\n");
        }
    }

    private void updateChannelDisplay(List<Channel> channels) {
        displayArea.setText("--- ACTIVE CHANNELS ---\n");
        for (Channel channel : channels) {
            displayArea.append("ID: " + channel.getId() + ", Name: " + channel.getName() + ", Owner: " + channel.getOwner().getUsername() + "\n");
        }
    }
}
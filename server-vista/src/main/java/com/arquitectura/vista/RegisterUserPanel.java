package com.arquitectura.vista;

import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto;
import com.arquitectura.controlador.ServerViewController;

import javax.swing.*;
import java.awt.*;

public class RegisterUserPanel extends JPanel {
    private final ServerViewController controller;

    public RegisterUserPanel(ServerViewController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Registrar Usuario");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, gbc);

        // Fila Usuario
        gbc.gridy++;
        gbc.gridwidth = 1;
        add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        JTextField userField = new JTextField(20);
        add(userField, gbc);

        // Fila Email
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(20);
        add(emailField, gbc);

        // Fila Contraseña
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(20);
        add(passField, gbc);

        // Fila Foto
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Foto (URL):"), gbc);

        gbc.gridx = 1;
        JTextField photoField = new JTextField(20);
        add(photoField, gbc);

        // Botón
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnCreateUser = new JButton("Crear Usuario");
        add(btnCreateUser, gbc);

        // Listener
        btnCreateUser.addActionListener(e -> {
            try {
                UserRegistrationRequestDto dto = new UserRegistrationRequestDto(
                        userField.getText(),
                        emailField.getText(),
                        new String(passField.getPassword())
                );
                controller.registrarNuevoUsuario(dto);
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Limpiar campos
                userField.setText("");
                emailField.setText("");
                passField.setText("");
                photoField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

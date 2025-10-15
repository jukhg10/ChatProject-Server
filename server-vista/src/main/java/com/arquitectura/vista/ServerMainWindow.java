package com.arquitectura.vista;


import com.arquitectura.controlador.ServerViewController;


import javax.swing.*;
import java.awt.*;


public class ServerMainWindow extends JFrame {
    private final ServerViewController controller;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // Paneles personalizados para cada vista
    private RegisterUserPanel registerUserPanel;
    private UsersReportPanel usersReportPanel;
    // Aquí añadirías los otros paneles de informes a medida que los crees

    public ServerMainWindow(ServerViewController controller) {
        this.controller = controller;
        initComponents();
        addListeners();
    }

    private void initComponents() {
        setTitle("Admin Panel - Chat Server");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Panel Izquierdo ---
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.WEST);

        // --- Panel Derecho (Contenido Principal) ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Creamos una instancia de nuestros paneles personalizados
        registerUserPanel = new RegisterUserPanel(controller);
        usersReportPanel = new UsersReportPanel(controller);
        // Aquí instanciarías los otros paneles

        // Añadimos los paneles al CardLayout con un nombre único
        mainContentPanel.add(registerUserPanel, "REGISTER_USER_PANEL");
        mainContentPanel.add(usersReportPanel, "USERS_REPORT_PANEL");
        // ... Añadir otros paneles aquí

        add(mainContentPanel, BorderLayout.CENTER);

        // Mostramos el panel de registro por defecto
        cardLayout.show(mainContentPanel, "REGISTER_USER_PANEL");
    }

    // Método para crear el panel de botones y mantener initComponents más limpio
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Creas todos tus botones aquí
        JButton btnShowRegister = new JButton("Registrar Usuario");
        JButton btnShowRegisteredUsers = new JButton("Usuarios Registrados");
        // ... otros botones

        panel.add(btnShowRegister);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(btnShowRegisteredUsers);
        // ... añadir otros botones

        // Asignamos los listeners aquí mismo
        btnShowRegister.addActionListener(e -> cardLayout.show(mainContentPanel, "REGISTER_USER_PANEL"));

        btnShowRegisteredUsers.addActionListener(e -> {
            usersReportPanel.refreshReport(); // Carga o refresca los datos
            cardLayout.show(mainContentPanel, "USERS_REPORT_PANEL");
        });

        return panel;
    }

    private void addListeners() {
        // El trabajo de los listeners ahora se puede hacer en createButtonPanel
        // para tener la lógica más localizada.
    }

    public void display() {
        setVisible(true);
    }
}
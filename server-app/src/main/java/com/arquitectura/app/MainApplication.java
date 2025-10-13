package com.arquitectura.app;

import com.arquitectura.controlador.ServerViewController;
import com.arquitectura.transporte.ServerListener;
import com.arquitectura.vista.ServerMainWindow;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.swing.*;

@SpringBootApplication(scanBasePackages = "com.arquitectura")
public class MainApplication {

    public static void main(String[] args) {
        // Esta parte ya está correcta
        ConfigurableApplicationContext context = new SpringApplicationBuilder(MainApplication.class)
                .headless(false).run(args);

        SwingUtilities.invokeLater(() -> {
            ServerViewController controller = context.getBean(ServerViewController.class);
            ServerMainWindow mainWindow = new ServerMainWindow(controller);
            mainWindow.display();
        });
    }

    // Este Bean ya está correcto
    @Bean
    public CommandLineRunner startServer(ServerListener serverListener) {
        return args -> {
            Thread serverThread = new Thread(() -> serverListener.startServer());
            serverThread.setDaemon(true);
            serverThread.start();
        };
    }
}
package com.arquitectura.transporte;

import com.arquitectura.controlador.IClientHandler;
import com.arquitectura.controlador.RequestDispatcher;
import com.arquitectura.logicaMensajes.eventos.BroadcastMessageEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@PropertySource("file:./config/server.properties")
@Component
public class ServerListener {
    @Value("${server.port}")
    private int port;
    @Value("${server.max.connections}")
    private int maxConnectedUsers;

    private ExecutorService clientPool;
    private final RequestDispatcher requestDispatcher;

    private final Map<Integer, IClientHandler> activeClientsById = Collections.synchronizedMap(new HashMap<>());

    public ServerListener(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }
    @PostConstruct
    public void init() {
        this.clientPool = Executors.newFixedThreadPool(maxConnectedUsers);
    }
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor de Chat iniciado en el puerto " + port + " con un límite de " + maxConnectedUsers + " conexiones.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                // Pasamos el método 'removeClient' como un callback al constructor del handler.
                ClientHandler clientHandler = new ClientHandler(clientSocket, requestDispatcher, this::removeClient);
                clientPool.submit(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Error fatal al iniciar el servidor: " + e.getMessage());
        }
    }
    /**
            * Escucha los eventos de broadcast publicados por la capa de lógica.
     * @param event El evento que contiene el mensaje a difundir.
     */
    @EventListener
    public void handleBroadcastEvent(BroadcastMessageEvent event) {
        System.out.println("Evento de Broadcast recibido. Enviando a " + activeClientsById.size() + " clientes autenticados.");

        // Enviamos el mensaje a todos los clientes que han iniciado sesión.
        // Iteramos sobre una copia de los valores para evitar problemas de concurrencia.
        for (IClientHandler handler : activeClientsById.values()) {
            handler.sendMessage(event.getFormattedMessage());
        }
    }
    //metodos publicos para gestion de sesiones

    public void registerAuthenticatedClient(int userId, IClientHandler handler) {
        activeClientsById.put(userId, handler);
        System.out.println("Usuario " + userId + " ha iniciado sesión. Clientes autenticados: " + activeClientsById.size());
    }


    public void removeAuthenticatedClient(int userId) {
        if (activeClientsById.remove(userId) != null) {
            System.out.println("Usuario " + userId + " ha cerrado sesión. Clientes autenticados: " + activeClientsById.size());
        }
    }

    //metodos privados para gestion de sesiones

    private void removeClient(IClientHandler clientHandler) {
        if (clientHandler.isAuthenticated()) {
            int userId = clientHandler.getAuthenticatedUser().getUserId();
            // Llama al método público para asegurar que el logging y la lógica sean consistentes.
            removeAuthenticatedClient(userId);
        }
        System.out.println("Cliente [" + clientHandler.getClientIpAddress() + "] desconectado.");
    }

}
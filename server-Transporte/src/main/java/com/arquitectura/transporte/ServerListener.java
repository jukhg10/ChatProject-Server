package com.arquitectura.transporte;

import com.arquitectura.controlador.RequestDispatcher;
import com.arquitectura.logica.eventos.BroadcastMessageEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ServerListener {
    private static final int PORT = 12345; // Puerto en el que escuchará el servidor
    private static final int MAX_CONNECTED_USERS = 50; // Límite de usuarios
    private final RequestDispatcher requestDispatcher;
    // Usamos un Set sincronizado para mantener una lista segura de los manejadores de cliente.
    private final Set<ClientHandler> activeClients = Collections.synchronizedSet(new HashSet<>());
    // Usamos un Object Pool (ThreadPool) para manejar los clientes de forma eficiente
    private final ExecutorService clientPool = Executors.newFixedThreadPool(MAX_CONNECTED_USERS);

    public ServerListener(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    @EventListener
    public void handleBroadcastEvent(BroadcastMessageEvent event) {
        System.out.println("Evento de Broadcast recibido. Enviando a los clientes...");
        broadcastMessage(event.getFormattedMessage());
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de Chat iniciado en el puerto " + PORT);

            while (true) { // Bucle para aceptar clientes continuamente
                Socket clientSocket = serverSocket.accept(); // Bloquea hasta que un cliente se conecte
                System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, requestDispatcher, this::removeClient);
                activeClients.add(clientHandler); // Lo añadimos a nuestra lista de clientes activos
                clientPool.submit(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }


    private void broadcastMessage(String message) {
        System.out.println("Enviando broadcast a " + activeClients.size() + " clientes.");
        synchronized (activeClients) {
            for (ClientHandler client : activeClients) {
                client.sendMessage(message);
            }
        }
    }


    // Método para que los ClientHandlers se eliminen de la lista al desconectarse
    private void removeClient(ClientHandler clientHandler) {
        activeClients.remove(clientHandler);
        System.out.println("Cliente desconectado. Clientes activos: " + activeClients.size());
    }
}
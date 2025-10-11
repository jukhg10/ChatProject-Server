package com.arquitectura.transporte;

import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ServerListener {

    private static final int PORT = 12345; // Puerto en el que escuchará el servidor
    private static final int MAX_CONNECTED_USERS = 50; // Límite de usuarios

    // Usamos un Object Pool (ThreadPool) para manejar los clientes de forma eficiente
    private final ExecutorService clientPool = Executors.newFixedThreadPool(MAX_CONNECTED_USERS);

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de Chat iniciado en el puerto " + PORT);

            while (true) { // Bucle para aceptar clientes continuamente
                Socket clientSocket = serverSocket.accept(); // Bloquea hasta que un cliente se conecte
                System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                // Por cada cliente, creamos un manejador y lo ejecutamos en nuestro pool de hilos.
                // Aquí necesitaríamos pasar el 'RequestDispatcher' al ClientHandler.
                // Lo haremos en el siguiente paso.
                // ClientHandler clientHandler = new ClientHandler(clientSocket, requestDispatcher);
                // clientPool.submit(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
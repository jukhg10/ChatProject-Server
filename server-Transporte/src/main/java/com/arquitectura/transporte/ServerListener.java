package com.arquitectura.transporte;

import com.arquitectura.controlador.RequestDispatcher; // <-- IMPORT THIS
import org.springframework.beans.factory.annotation.Autowired; // <-- IMPORT THIS
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ServerListener {

    private static final int PORT = 12345;
    private static final int MAX_CONNECTED_USERS = 50;
    private final ExecutorService clientPool = Executors.newFixedThreadPool(MAX_CONNECTED_USERS);

    // --- ADD THIS SECTION ---
    private final RequestDispatcher requestDispatcher;

    @Autowired
    public ServerListener(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }
    // --- END SECTION ---

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de Chat iniciado en el puerto " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                // --- UNCOMMENT AND COMPLETE THIS PART ---
                // For each client, create a handler and run it in our thread pool.
                ClientHandler clientHandler = new ClientHandler(clientSocket, requestDispatcher);
                clientPool.submit(clientHandler);
                // --- END PART ---
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
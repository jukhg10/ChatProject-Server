package com.arquitectura.transporte;

import com.arquitectura.controlador.IClientHandler; // <-- Asegúrate de que este import exista
import com.arquitectura.controlador.RequestDispatcher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable, IClientHandler {

    private final Socket clientSocket;
    private final RequestDispatcher requestDispatcher;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, RequestDispatcher dispatcher) {
        this.clientSocket = socket;
        this.requestDispatcher = dispatcher;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                requestDispatcher.dispatch(inputLine, this);
            }

        } catch (Exception e) {
            System.out.println("Cliente desconectado: " + e.getMessage());
        } finally {
            // Código para cerrar la conexión
        }
    }

    @Override
    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public String getClientIpAddress() {
        return clientSocket.getInetAddress().getHostAddress();
    }
}
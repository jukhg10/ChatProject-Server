package com.arquitectura.controlador;

import com.arquitectura.fachada.IChatFachada;
// We no longer need to import the concrete ClientHandler here
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestDispatcher {

    private final IChatFachada chatFachada;

    @Autowired
    public RequestDispatcher(IChatFachada chatFachada) {
        this.chatFachada = chatFachada;
    }

    // Change the type here
    public void dispatch(String request, IClientHandler handler) {
        // ... the rest of the code in this method is IDENTICAL ...
        String[] parts = request.split(";");
        String command = parts[0].toUpperCase();

        try {
            switch (command) {
                case "REGISTRAR":
                    if (parts.length == 4) {
                        // ... code ...
                        String ipAddress = handler.getClientIpAddress(); // This still works
                        chatFachada.registrarUsuario(parts[1], parts[2], parts[3], ipAddress);
                        handler.sendMessage("OK;Usuario registrado exitosamente."); // This still works
                    } else {
                        handler.sendMessage("ERROR;Formato incorrecto para REGISTRAR.");
                    }
                    break;
                // ... other cases ...
                default:
                    handler.sendMessage("ERROR;Comando desconocido: " + command);
                    break;
            }
        } catch (Exception e) {
            handler.sendMessage("ERROR;" + e.getMessage());
        }
    }
}
package com.arquitectura.controlador;

import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto; // <-- IMPORT AÑADIDO
import com.arquitectura.fachada.IChatFachada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestDispatcher {

    private final IChatFachada chatFachada;

    @Autowired
    public RequestDispatcher(IChatFachada chatFachada) {
        this.chatFachada = chatFachada;
    }

    public void dispatch(String request, IClientHandler handler) {
        String[] parts = request.split(";");
        String command = parts[0].toUpperCase();

        try {
            switch (command) {
                case "REGISTRAR":
                    if (parts.length == 4) {
                        // 1. Se crea el DTO a partir de los datos de la petición.
                        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                            parts[1], // username
                            parts[2], // email
                            parts[3]  // password
                        );
                        
                        String ipAddress = handler.getClientIpAddress();
                        
                        // 2. Se llama a la fachada con el DTO.
                        chatFachada.registrarUsuario(requestDto, ipAddress);
                        
                        handler.sendMessage("OK;Usuario registrado exitosamente.");
                    } else {
                        handler.sendMessage("ERROR;Formato incorrecto para REGISTRAR. Se esperaba: REGISTRAR;username;email;password");
                    }
                    break;

                // Aquí irían los otros casos para "LOGIN", "SEND_MESSAGE", etc.
                
                default:
                    handler.sendMessage("ERROR;Comando desconocido: " + command);
                    break;
            }
        } catch (Exception e) {
            handler.sendMessage("ERROR;" + e.getMessage());
        }
    }
}
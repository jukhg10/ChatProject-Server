package com.arquitectura.controlador;

import com.arquitectura.DTO.Mensajes.SendMessageRequestDto;
import com.arquitectura.DTO.usuarios.LoginRequestDto;
import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto; // <-- IMPORT AÑADIDO
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.fachada.IChatFachada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestDispatcher {

    private final IChatFachada chatFachada;
    private UserResponseDto authenticatedUser = null;

    @Autowired
    public RequestDispatcher(IChatFachada chatFachada) {
        this.chatFachada = chatFachada;
    }

    public void dispatch(String request, IClientHandler handler) {
        String[] parts = request.split(";", 2); // Dividir solo en 2 para el comando y el resto
        String command = parts[0].toUpperCase();

        try {// --- VALIDACIÓN DE SESIÓN ---
            // Si el comando no es LOGIN y el usuario no está autenticado, se rechaza.
            if (!command.equals("LOGIN") && !handler.isAuthenticated()) {
                handler.sendMessage("ERROR;NO_AUTH;Debes iniciar sesión para realizar esta acción.");
                return;
            }

            switch (command) {

                case "LOGIN":
                    if (parts.length == 2) {
                        String[] credentials = parts[1].split(";", 2);
                        if (credentials.length == 2) {
                            // 1. Crear el DTO a partir de los datos de la petición
                            LoginRequestDto loginDto = new LoginRequestDto(
                                    credentials[0], // username
                                    credentials[1]  // password
                            );
                            String ipAddress = handler.getClientIpAddress();

                            // 2. Llamar a la fachada con el DTO
                            UserResponseDto userDto = chatFachada.autenticarUsuario(loginDto, ipAddress);

                            // 3. Marcar el handler como autenticado
                            handler.setAuthenticatedUser(userDto);

                            // 4. Enviar respuesta de éxito al cliente
                            // Es útil devolver el ID y el username para que el cliente los guarde
                            handler.sendMessage("OK;LOGIN_SUCCESS;" + userDto.getUserId() + ";" + userDto.getUsername());


                        }
                    }
                    break;
                case "LOGOUT":
                    // Opcional: Podrías notificar a otros usuarios que este se ha desconectado.
                    // Por ahora, solo limpiamos el estado del servidor.

                    String username = handler.getAuthenticatedUser().getUsername();
                    handler.clearAuthenticatedUser(); // "Olvida" quién era en esta conexión.

                    handler.sendMessage("OK;LOGOUT;Sesión de " + username + " cerrada.");
                    break;
                case "ENVIAR_MENSAJE_TEXTO":
                    // Ejemplo de cómo usar la sesión en otros comandos
                    String[] messageParts = parts[1].split(";", 2);
                    if (messageParts.length == 2) {
                        int channelId = Integer.parseInt(messageParts[0]);
                        String content = messageParts[1];

                        // Obtenemos el ID del autor desde el handler, que ya sabe quién es.
                        int autorId = handler.getAuthenticatedUser().getUserId();

                        SendMessageRequestDto messageDto = new SendMessageRequestDto(channelId, "TEXT", content);
                        chatFachada.enviarMensajeTexto(messageDto, autorId);

                        handler.sendMessage("OK;ENVIAR_MENSAJE_TEXTO;Mensaje enviado.");
                    }
                    break;


                // Aquí irían los otros casos para "SEND_MESSAGE", etc.

                default:
                    handler.sendMessage("ERROR;Comando desconocido: " + command);
                    break;
            }
        } catch (Exception e) {
            // Si autenticarUsuario lanza una excepción, se captura aquí
            handler.sendMessage("ERROR;" + e.getMessage());
        }
    }
}
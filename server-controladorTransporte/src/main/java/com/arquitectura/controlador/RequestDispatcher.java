package com.arquitectura.controlador;

import com.arquitectura.DTO.Mensajes.SendMessageRequestDto;
import com.arquitectura.DTO.canales.ChannelResponseDto;
import com.arquitectura.DTO.canales.CreateChannelRequestDto;
import com.arquitectura.DTO.canales.InviteMemberRequestDto;
import com.arquitectura.DTO.canales.RespondToInviteRequestDto;
import com.arquitectura.DTO.usuarios.LoginRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.fachada.IChatFachada;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestDispatcher {

    private final IChatFachada chatFachada;
    private Gson gson =new Gson() ;
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
                            UserResponseDto userDto = chatFachada.autenticarUsuario(loginDto, ipAddress);
                            handler.setAuthenticatedUser(userDto);
                            handler.sendMessage("OK;LOGIN_SUCCESS;" + userDto.getUserId() + ";" + userDto.getUsername());
                        }
                    }
                    break;
                case "LOGOUT":
                    String username = handler.getAuthenticatedUser().getUsername();
                    handler.clearAuthenticatedUser();
                    handler.sendMessage("OK;LOGOUT;Sesión de " + username + " cerrada.");
                    break;
                case "OBTENER_USUARIOS":
                    // 1. Llamar a la fachada para obtener la lista de DTOs de usuario
                    List<UserResponseDto> usuarios = chatFachada.obtenerTodosLosUsuarios();
                    String jsonUsuarios = gson.toJson(usuarios);
                    // 3. Enviar la lista al cliente
                    handler.sendMessage("OK;OBTENER_USUARIOS;" + jsonUsuarios);
                    break;

                case "CREAR_CANAL_GRUPO":
                    if (parts.length == 2) {
                        String channelName = parts[1];
                        //  Obtener el ID del creador desde la sesión del handler
                        int ownerId = handler.getAuthenticatedUser().getUserId();
                        CreateChannelRequestDto createDto = new CreateChannelRequestDto(channelName, "GRUPO");
                        ChannelResponseDto channelDto = chatFachada.crearCanal(createDto, ownerId);
                        // Enviar una respuesta de éxito al cliente con los datos del nuevo canal
                        handler.sendMessage("OK;CREAR_CANAL_GRUPO;" + channelDto.getChannelId() + ";" + channelDto.getChannelName());
                    } else {
                        handler.sendMessage("ERROR;CREAR_CANAL_GRUPO;Formato incorrecto. Se esperaba: CREAR_CANAL_GRUPO;nombre_canal");
                    }
                    break;
                case "CREAR_CANAL_DIRECTO":
                    if (parts.length == 2) {
                        // 1. Obtener el ID del otro usuario desde el comando
                        int otherUserId = Integer.parseInt(parts[1]);
                        // 2. Obtener el ID del usuario actual desde la sesión
                        int currentUserId = handler.getAuthenticatedUser().getUserId();
                        // 3. Llamar a la fachada para crear el canal directo
                        ChannelResponseDto directChannelDto = chatFachada.crearCanalDirecto(currentUserId, otherUserId);
                        // 4. Enviar la respuesta de éxito al cliente
                        handler.sendMessage("OK;CREAR_CANAL_DIRECTO;" + directChannelDto.getChannelId() + ";" + directChannelDto.getChannelName());
                    } else {
                        handler.sendMessage("ERROR;CREAR_CANAL_DIRECTO;Formato incorrecto. Se esperaba: CREAR_CANAL_DIRECTO;id_otro_usuario");
                    }
                    break;
                case "OBTENER_MIS_CANALES":
                    // 1. Obtener el ID del usuario que hace la petición desde su sesión.
                    int userId = handler.getAuthenticatedUser().getUserId();

                    // 2. Llamar a la fachada para obtener la lista de sus canales.
                    List<ChannelResponseDto> misCanales = chatFachada.obtenerCanalesPorUsuario(userId);

                    // 3. Convertir la lista de objetos DTO a una cadena JSON para el transporte.
                    String jsonCanales = gson.toJson(misCanales);

                    // 4. Enviar la respuesta al cliente.
                    handler.sendMessage("OK;OBTENER_MIS_CANALES;" + jsonCanales);
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
                case "INVITAR_USUARIO":
                    if (parts.length == 2) {
                        String[] params = parts[1].split(";", 2);
                        int channelId = Integer.parseInt(params[0]);
                        int userIdToInvite = Integer.parseInt(params[1]);
                        int ownerId = handler.getAuthenticatedUser().getUserId();

                        InviteMemberRequestDto inviteDto = new InviteMemberRequestDto(channelId, userIdToInvite);
                        chatFachada.invitarMiembro(inviteDto, ownerId);

                        handler.sendMessage("OK;INVITAR_USUARIO;Invitación enviada.");
                    }
                    break;

                case "OBTENER_MIS_INVITACIONES":
                    int userIdInvitacion = handler.getAuthenticatedUser().getUserId();
                    List<ChannelResponseDto> invitaciones = chatFachada.getPendingInvitationsForUser(userIdInvitacion);
                    String jsonInvitaciones = gson.toJson(invitaciones);
                    handler.sendMessage("OK;OBTENER_MIS_INVITACIONES;" + jsonInvitaciones);
                    break;

                case "RESPONDER_INVITACION":
                    if (parts.length == 2) {
                        String[] params = parts[1].split(";", 2);
                        int channelId = Integer.parseInt(params[0]);
                        boolean accepted = params[1].equalsIgnoreCase("ACEPTAR");
                        int currentUserId = handler.getAuthenticatedUser().getUserId();

                        RespondToInviteRequestDto responseDto = new RespondToInviteRequestDto(channelId, accepted);
                        chatFachada.responderInvitacion(responseDto, currentUserId);

                        handler.sendMessage("OK;RESPONDER_INVITACION;Respuesta procesada.");
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
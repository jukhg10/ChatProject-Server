package com.arquitectura.fachada;

import com.arquitectura.DTO.canales.CreateChannelRequestDto;
import com.arquitectura.DTO.Mensajes.SendMessageRequestDto; // <-- IMPORT AÑADIDO
import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto;
import com.arquitectura.domain.Channel;
import com.arquitectura.domain.Message;
import com.arquitectura.domain.User;

import java.util.List;
import java.util.Optional;

public interface IChatFachada {

    // --- Métodos de Usuario ---
    User registrarUsuario(UserRegistrationRequestDto requestDto, String ipAddress) throws Exception;
    Optional<User> buscarUsuarioPorUsername(String username);
    List<User> obtenerTodosLosUsuarios();

    // --- Métodos de Canal ---
    Channel crearCanal(CreateChannelRequestDto requestDto, User owner);
    Channel agregarMiembroACanal(int channelId, int userId) throws Exception;
    List<Channel> obtenerTodosLosCanales();

    // --- MÉTODOS DE MENSAJE (ACTUALIZADOS) ---
    // Se reemplazan los dos métodos anteriores por uno solo que usa DTO
    Message enviarMensajeTexto(SendMessageRequestDto requestDto, int autorId) throws Exception;
    Message enviarMensajeAudio(SendMessageRequestDto requestDto, int autorId) throws Exception;
    List<Message> obtenerMensajesDeCanal(int canalId);
}
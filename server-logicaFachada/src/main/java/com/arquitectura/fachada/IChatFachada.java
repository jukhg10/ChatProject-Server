package com.arquitectura.fachada;

import com.arquitectura.DTO.Mensajes.MessageResponseDto;
import com.arquitectura.DTO.canales.ChannelResponseDto;
import com.arquitectura.DTO.canales.CreateChannelRequestDto;
import com.arquitectura.DTO.Mensajes.SendMessageRequestDto; // <-- IMPORT AÑADIDO
import com.arquitectura.DTO.canales.InviteMemberRequestDto;
import com.arquitectura.DTO.usuarios.LoginRequestDto;
import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;


import java.util.List;
import java.util.Optional;

public interface IChatFachada {

    // --- Métodos de Usuario ---
    UserResponseDto registrarUsuario(UserRegistrationRequestDto requestDto, String ipAddress) throws Exception;
    Optional<UserResponseDto> buscarUsuarioPorUsername(String username);
    List<UserResponseDto> obtenerTodosLosUsuarios();
    UserResponseDto autenticarUsuario(LoginRequestDto requestDto, String ipAddress) throws Exception;

    // --- Métodos de Canal ---
    ChannelResponseDto crearCanal(CreateChannelRequestDto requestDto, int ownerId) throws Exception;
    void agregarMiembroACanal(InviteMemberRequestDto inviteMemberRequestDto, int userId) throws Exception;
    List<ChannelResponseDto> obtenerTodosLosCanales();

    // --- MÉTODOS DE MENSAJE (ACTUALIZADOS) ---
    // Se reemplazan los dos métodos anteriores por uno solo que usa DTO
    void enviarMensajeBroadcast(String contenido, int adminId) throws Exception;
    MessageResponseDto enviarMensajeTexto(SendMessageRequestDto requestDto, int autorId) throws Exception;
    MessageResponseDto enviarMensajeAudio(SendMessageRequestDto requestDto, int autorId) throws Exception;
    List<MessageResponseDto> obtenerMensajesDeCanal(int canalId);
}
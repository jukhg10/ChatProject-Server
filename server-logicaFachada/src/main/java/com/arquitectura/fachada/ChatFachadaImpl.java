package com.arquitectura.fachada;

import com.arquitectura.DTO.Mensajes.MessageResponseDto;
import com.arquitectura.DTO.canales.ChannelResponseDto;
import com.arquitectura.DTO.canales.CreateChannelRequestDto;
import com.arquitectura.DTO.Mensajes.SendMessageRequestDto;
import com.arquitectura.DTO.canales.InviteMemberRequestDto;
import com.arquitectura.DTO.usuarios.LoginRequestDto;
import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.logicaCanales.IChannelService;
import com.arquitectura.logicaMensajes.IMessageService;
import com.arquitectura.logicaUsuarios.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ChatFachadaImpl implements IChatFachada {

    private final IUserService userService;
    private final IChannelService channelService;
    private final IMessageService messageService;

    @Autowired
    public ChatFachadaImpl(IUserService userService, IChannelService channelService, IMessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    // Metodos de usuario
    @Override
    public UserResponseDto registrarUsuario(UserRegistrationRequestDto requestDto, String ipAddress) throws Exception {
        return userService.registrarUsuario(requestDto, ipAddress);
    }

    @Override
    public Optional<UserResponseDto> buscarUsuarioPorUsername(String username) {
        return userService.buscarPorUsername(username);
    }

    @Override
    public List<UserResponseDto> obtenerTodosLosUsuarios() {
        return userService.obtenerTodosLosUsuarios();
    }

    @Override
    public UserResponseDto autenticarUsuario(LoginRequestDto requestDto, String ipAddress) throws Exception {
        return userService.autenticarUsuario(requestDto, ipAddress);
    }


    // --- MÉTODOS DE Canales ---
    @Override
    public ChannelResponseDto crearCanal(CreateChannelRequestDto requestDto, int ownerId) throws Exception {
        return channelService.crearCanal(requestDto, ownerId);
    }

    @Override
    public void agregarMiembroACanal(InviteMemberRequestDto inviteMemberRequestDto, int userId) throws Exception {
        channelService.invitarMiembro(inviteMemberRequestDto, userId);
    }

    @Override
    public List<ChannelResponseDto> obtenerTodosLosCanales() {
        return channelService.obtenerTodosLosCanales();
    }

    
    // ---Metodos de Mensajes---
    @Override
    public MessageResponseDto enviarMensajeTexto(SendMessageRequestDto requestDto, int autorId) throws Exception {
        return messageService.enviarMensajeTexto(requestDto, autorId);
    }

    @Override
    public MessageResponseDto enviarMensajeAudio(SendMessageRequestDto requestDto, int autorId) throws Exception {
        return messageService.enviarMensajeAudio(requestDto, autorId);
    }

    @Override
    public List<MessageResponseDto> obtenerMensajesDeCanal(int canalId) {
        return messageService.obtenerMensajesPorCanal(canalId);
    }

    @Override
    public void enviarMensajeBroadcast(String contenido, int adminId) throws Exception {
        messageService.enviarMensajeBroadcast(contenido, adminId);
    }
}
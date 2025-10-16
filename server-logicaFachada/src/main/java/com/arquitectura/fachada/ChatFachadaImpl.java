package com.arquitectura.fachada;

import com.arquitectura.DTO.Mensajes.MessageResponseDto;
import com.arquitectura.DTO.Mensajes.TranscriptionResponseDto;
import com.arquitectura.DTO.canales.ChannelResponseDto;
import com.arquitectura.DTO.canales.CreateChannelRequestDto;
import com.arquitectura.DTO.Mensajes.SendMessageRequestDto;
import com.arquitectura.DTO.canales.InviteMemberRequestDto;
import com.arquitectura.DTO.canales.RespondToInviteRequestDto;
import com.arquitectura.DTO.usuarios.LoginRequestDto;
import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.logicaCanales.IChannelService;
import com.arquitectura.logicaMensajes.IMessageService;
import com.arquitectura.logicaMensajes.transcripcionAudio.IAudioTranscriptionService;
import com.arquitectura.logicaUsuarios.IUserService;
import com.arquitectura.utils.file.IFileStorageService;
import com.arquitectura.utils.logs.ILogService;
import com.arquitectura.utils.logs.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class ChatFachadaImpl implements IChatFachada {

    private final IUserService userService;
    private final IChannelService channelService;
    private final IMessageService messageService;
    private final IAudioTranscriptionService transcriptionService;
    private final IFileStorageService fileStorageService;

    private final ILogService logService ;

    @Autowired
    public ChatFachadaImpl(IUserService userService, IChannelService channelService, IMessageService messageService, IAudioTranscriptionService transcriptionService, IFileStorageService fileStorageService, ILogService logService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
        this.transcriptionService = transcriptionService;
        this.fileStorageService = fileStorageService;
        this.logService = logService;
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
    @Override
    public List<UserResponseDto> getUsersByIds(Set<Integer> userIds) {
        return userService.getUsersByIds(userIds);
    }

    // --- MÉTODOS DE Canales ---
    @Override
    public ChannelResponseDto crearCanal(CreateChannelRequestDto requestDto, int ownerId) throws Exception {
        return channelService.crearCanal(requestDto, ownerId);
    }
    @Override
    public ChannelResponseDto crearCanalDirecto(int user1Id, int user2Id) throws Exception {
        return channelService.crearCanalDirecto(user1Id, user2Id);
    }

    @Override
    public void agregarMiembroACanal(InviteMemberRequestDto inviteMemberRequestDto, int userId) throws Exception {
        channelService.invitarMiembro(inviteMemberRequestDto, userId);
    }

    @Override
    public List<ChannelResponseDto> obtenerTodosLosCanales() {
        return channelService.obtenerTodosLosCanales();
    }
    @Override
    public Map<ChannelResponseDto, List<UserResponseDto>> obtenerCanalesConMiembros() {
        return channelService.obtenerCanalesConMiembros();
    }
    @Override
    public List<ChannelResponseDto> obtenerCanalesPorUsuario(int userId) {
        // La fachada simplemente delega la llamada al servicio de canales.
        return channelService.obtenerCanalesPorUsuario(userId);
    }
    @Override
    public void invitarMiembro(InviteMemberRequestDto requestDto, int ownerId) throws Exception {
        channelService.invitarMiembro(requestDto, ownerId);
    }
    @Override
    public void responderInvitacion(RespondToInviteRequestDto requestDto, int userId) throws Exception {
        channelService.responderInvitacion(requestDto, userId);
    }
    @Override
    public List<ChannelResponseDto> getPendingInvitationsForUser(int userId) {
        return channelService.getPendingInvitationsForUser(userId);
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
    @Override
    public List<TranscriptionResponseDto> obtenerTranscripciones() {
        return transcriptionService.getAllTranscriptions();
    }

    @Override
    public String getFileAsBase64(String relativePath) {
        try {
            return fileStorageService.readFileAsBase64(relativePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // --- MÉTODOS PARA INFORMES ---
    @Override
    public String getLogContents() throws IOException {
        return logService.getLogContents();
    }
}
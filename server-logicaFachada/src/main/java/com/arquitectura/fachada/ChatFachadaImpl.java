package com.arquitectura.fachada;

import com.arquitectura.DTO.canales.CreateChannelRequestDto;
import com.arquitectura.DTO.Mensajes.SendMessageRequestDto;
import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.domain.Channel;
import com.arquitectura.domain.Message;
import com.arquitectura.domain.User;
import com.arquitectura.logica.IChannelService;
import com.arquitectura.logica.IMessageService;
import com.arquitectura.logica.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // ... (métodos de usuario y canal sin cambios) ...

    // --- MÉTODOS DE MENSAJE (ACTUALIZADOS) ---
    @Override
    public Message enviarMensajeTexto(SendMessageRequestDto requestDto, int autorId) throws Exception {
        // La fachada ahora delega la llamada al método correspondiente del servicio
        return messageService.enviarMensajeTexto(requestDto, autorId);
    }

    @Override
    public Message enviarMensajeAudio(SendMessageRequestDto requestDto, int autorId) throws Exception {
        // La fachada ahora delega la llamada al método correspondiente del servicio
        return messageService.enviarMensajeAudio(requestDto, autorId);
    }

    @Override
    public List<Message> obtenerMensajesDeCanal(int canalId) {
        return messageService.obtenerMensajesPorCanal(canalId);
    }
    
    // --- Implementación del resto de métodos ---
    
    @Override
    public User registrarUsuario(UserRegistrationRequestDto requestDto, String ipAddress) throws Exception {
        UserResponseDto responseDto = userService.registrarUsuario(requestDto, ipAddress);
        return userService.findEntityById(responseDto.getUserId())
                .orElseThrow(() -> new IllegalStateException("No se pudo encontrar el usuario recién registrado."));
    }

    @Override
    public Optional<User> buscarUsuarioPorUsername(String username) {
        return userService.buscarPorUsername(username)
                .flatMap(dto -> userService.findEntityById(dto.getUserId()));
    }

    @Override
    public List<User> obtenerTodosLosUsuarios() {
        return userService.obtenerTodosLosUsuarios().stream()
               .map(dto -> userService.findEntityById(dto.getUserId()))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .collect(Collectors.toList());
    }

    @Override
    public Channel crearCanal(CreateChannelRequestDto requestDto, User owner) {
        return channelService.crearCanal(requestDto, owner);
    }

    @Override
    public Channel agregarMiembroACanal(int channelId, int userId) throws Exception {
        return channelService.agregarMiembro(channelId, userId);
    }

    @Override
    public List<Channel> obtenerTodosLosCanales() {
        return channelService.obtenerTodosLosCanales();
    }
}
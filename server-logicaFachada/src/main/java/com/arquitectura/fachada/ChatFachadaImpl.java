package com.arquitectura.fachada;

import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto; // <-- IMPORT AÑADIDO
import com.arquitectura.DTO.usuarios.UserResponseDto;      // <-- IMPORT AÑADIDO
import com.arquitectura.domain.Channel;
import com.arquitectura.domain.Message;
import com.arquitectura.domain.User;
import com.arquitectura.domain.enums.TipoCanal;
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

    @Override
    public User registrarUsuario(String username, String email, String password, String ipAddress) throws Exception {
        // 1. La fachada crea el DTO para comunicarse con el servicio
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(username, email, password);
        UserResponseDto responseDto = userService.registrarUsuario(requestDto, ipAddress);

        // 2. Para cumplir su contrato de devolver una Entidad, la busca usando el ID del DTO de respuesta.
        return userService.findEntityById(responseDto.getUserId())
                .orElseThrow(() -> new IllegalStateException("No se pudo encontrar el usuario recién registrado."));
    }

    @Override
    public Optional<User> buscarUsuarioPorUsername(String username) {
        // Llama al servicio que devuelve un DTO, y si existe, busca la entidad completa.
        return userService.buscarPorUsername(username)
                .flatMap(dto -> userService.findEntityById(dto.getUserId()));
    }
    
    @Override
    public List<User> obtenerTodosLosUsuarios() {
        // Obtiene la lista de DTOs
        List<UserResponseDto> userDtos = userService.obtenerTodosLosUsuarios();
        // Mapea la lista de DTOs a una lista de Entidades
        return userDtos.stream()
               .map(dto -> userService.findEntityById(dto.getUserId()))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .collect(Collectors.toList());
    }

    @Override
    public Channel crearCanal(String channelName, User owner, TipoCanal tipo) {
        return channelService.crearCanal(channelName, owner, tipo);
    }

    // ... los demás métodos de la fachada no cambian ...
    @Override
    public Channel agregarMiembroACanal(int channelId, int userId) throws Exception {
        return channelService.agregarMiembro(channelId, userId);
    }

    @Override
    public List<Channel> obtenerTodosLosCanales() {
        return channelService.obtenerTodosLosCanales();
    }

    @Override
    public Message enviarMensajeTexto(String contenido, int autorId, int canalId) throws Exception {
        return messageService.enviarMensajeTexto(contenido, autorId, canalId);
    }

    @Override
    public Message enviarMensajeAudio(String urlAudio, int autorId, int canalId) throws Exception {
        return messageService.enviarMensajeAudio(urlAudio, autorId, canalId);
    }

    @Override
    public List<Message> obtenerMensajesDeCanal(int canalId) {
        return messageService.obtenerMensajesPorCanal(canalId);
    }
}
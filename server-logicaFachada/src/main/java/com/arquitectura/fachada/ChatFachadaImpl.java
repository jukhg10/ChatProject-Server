package com.arquitectura.fachada;

import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.domain.Channel;
import com.arquitectura.domain.Message;
import com.arquitectura.domain.User;
import com.arquitectura.logica.IChannelService;
import com.arquitectura.logica.IMessageService;
import com.arquitectura.logica.IUserService;
import com.arquitectura.persistence.UserRepository; // Import the repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ChatFachadaImpl implements IChatFachada {

    private final IUserService userService;
    private final IChannelService channelService;
    private final IMessageService messageService;
    private final UserRepository userRepository; // Inject the repository

    @Autowired
    public ChatFachadaImpl(IUserService userService, IChannelService channelService, IMessageService messageService, UserRepository userRepository) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
        this.userRepository = userRepository; // Initialize it
    }

    // --- User Methods ---

    @Override
    public User registrarUsuario(String username, String email, String password, String ipAddress) throws Exception {
        // 1. Create the DTO for the service.
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(username, email, password);
        
        // 2. Call the service, which returns a DTO of the newly created user.
        UserResponseDto responseDto = userService.registrarUsuario(requestDto, ipAddress);

        // 3. Use the ID from the DTO to fetch and return the full, managed User entity.
        return userRepository.findById(responseDto.getUserId())
                .orElseThrow(() -> new IllegalStateException("Could not find user after registration"));
    }

    @Override
    public Optional<User> buscarUsuarioPorUsername(String username) {
        // 1. Get the DTO from the service.
        Optional<UserResponseDto> userDtoOpt = userService.buscarPorUsername(username);

        // 2. If the DTO exists, use its ID to find the *real* User entity from the database.
        return userDtoOpt.flatMap(dto -> userRepository.findById(dto.getUserId()));
    }

    @Override
    public List<User> obtenerTodosLosUsuarios() {
        // 1. Get the list of DTOs from the service.
        List<UserResponseDto> userDtos = userService.obtenerTodosLosUsuarios();
        if (userDtos.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Extract all the user IDs from the DTOs.
        List<Integer> userIds = userDtos.stream()
                                        .map(UserResponseDto::getUserId)
                                        .collect(Collectors.toList());

        // 3. Fetch all User entities from the database in a single query.
        return userRepository.findAllById(userIds);
    }

    // --- Channel Methods (Unchanged) ---
    @Override
    public Channel crearCanal(String channelName, User owner) {
        return channelService.crearCanal(channelName, owner);
    }

    @Override
    public Channel agregarMiembroACanal(int channelId, int userId) throws Exception {
        return channelService.agregarMiembro(channelId, userId);
    }

    @Override
    public List<Channel> obtenerTodosLosCanales() {
        return channelService.obtenerTodosLosCanales();
    }

    // --- Message Methods (Unchanged) ---
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
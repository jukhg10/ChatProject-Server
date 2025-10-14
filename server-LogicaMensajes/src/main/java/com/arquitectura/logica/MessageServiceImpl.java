package com.arquitectura.logica;

import com.arquitectura.DTO.Mensajes.MessageResponseDto;
import com.arquitectura.DTO.Mensajes.SendMessageRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.domain.*;
import com.arquitectura.logica.eventos.BroadcastMessageEvent;
import com.arquitectura.persistence.ChannelRepository;
import com.arquitectura.persistence.MessageRepository;
import com.arquitectura.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements IMessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository, ApplicationEventPublisher eventPublisher) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public MessageResponseDto enviarMensajeTexto(SendMessageRequestDto requestDto, int autorId) throws Exception {
        User autor = userRepository.findById(autorId)
                .orElseThrow(() -> new Exception("El autor con ID " + autorId + " no existe."));
        
        Channel canal = channelRepository.findById(requestDto.getChannelId())
                .orElseThrow(() -> new Exception("El canal con ID " + requestDto.getChannelId() + " no existe."));

        TextMessage nuevoMensaje = new TextMessage(autor, canal, requestDto.getContent());
        Message mensajeGuardado = messageRepository.save(nuevoMensaje);

        return mapToMessageResponseDto(mensajeGuardado);
    }

    @Override
    @Transactional
    public MessageResponseDto enviarMensajeAudio(SendMessageRequestDto requestDto, int autorId) throws Exception {
        User autor = userRepository.findById(autorId)
                .orElseThrow(() -> new Exception("El autor con ID " + autorId + " no existe."));
        
        Channel canal = channelRepository.findById(requestDto.getChannelId())
                .orElseThrow(() -> new Exception("El canal con ID " + requestDto.getChannelId() + " no existe."));

        AudioMessage nuevoMensaje = new AudioMessage(autor, canal, requestDto.getContent());
        Message mensajeGuardado = messageRepository.save(nuevoMensaje);
        return mapToMessageResponseDto(mensajeGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDto> obtenerMensajesPorCanal(int canalId) {
        return messageRepository.findByChannelChannelId(canalId).stream()
                .map(this::mapToMessageResponseDto) // Mapea cada mensaje a su DTO
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void enviarMensajeBroadcast(String contenido, int adminId) throws Exception {
        // Asumimos que el canal de broadcast tiene un ID conocido, por ejemplo, 1
        final int BROADCAST_CHANNEL_ID = 1;

        Channel canal = channelRepository.findById(BROADCAST_CHANNEL_ID)
                .orElseThrow(() -> new Exception("El canal de broadcast no existe."));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new Exception("El usuario administrador no existe."));

        TextMessage broadcastMessage = new TextMessage(admin, canal, contenido);

        // 1. Guardamos el mensaje en la BD
        messageRepository.save(broadcastMessage);

        // 2. Publicamos un evento. La capa de lógica no sabe quién lo escuchará.
        String formattedMessage = "BROADCAST;" + admin.getUsername() + ";" + contenido;
        eventPublisher.publishEvent(new BroadcastMessageEvent(this, formattedMessage));
    }


    private MessageResponseDto mapToMessageResponseDto(Message message) {
        // Mapea el autor a su DTO correspondiente para no exponer la entidad User
        UserResponseDto authorDto = new UserResponseDto(
                message.getAuthor().getUserId(),
                message.getAuthor().getUsername(),
                message.getAuthor().getEmail(),
                message.getAuthor().getPhotoAddress()
        );

        String messageType = "";
        String content = "";

        // Determina el tipo y contenido basado en la clase hija
        if (message instanceof TextMessage) {
            messageType = "TEXT";
            content = ((TextMessage) message).getContent();
        } else if (message instanceof AudioMessage) {
            messageType = "AUDIO";
            content = ((AudioMessage) message).getAudioUrl();
        }

        return new MessageResponseDto(
                message.getIdMensaje(),
                message.getChannel().getChannelId(),
                authorDto,
                message.getTimestamp(),
                messageType,
                content
        );
    }
}
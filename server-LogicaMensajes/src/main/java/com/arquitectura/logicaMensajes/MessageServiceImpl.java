package com.arquitectura.logicaMensajes;

import com.arquitectura.DTO.Mensajes.MessageResponseDto;
import com.arquitectura.DTO.Mensajes.SendMessageRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.domain.*;
import com.arquitectura.domain.enums.EstadoMembresia;
import com.arquitectura.events.BroadcastMessageEvent;
import com.arquitectura.events.NewMessageEvent;
import com.arquitectura.logicaMensajes.transcripcionAudio.AudioTranscriptionService;
import com.arquitectura.persistence.ChannelRepository;
import com.arquitectura.persistence.MembresiaCanalRepository;
import com.arquitectura.persistence.MessageRepository;
import com.arquitectura.persistence.UserRepository;
import com.arquitectura.utils.file.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements IMessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MembresiaCanalRepository membresiaCanalRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final FileStorageService fileStorageService;
    private final AudioTranscriptionService transcriptionService;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository, MembresiaCanalRepository membresiaCanalRepository, ApplicationEventPublisher eventPublisher, FileStorageService fileStorageService, AudioTranscriptionService transcriptionService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.membresiaCanalRepository = membresiaCanalRepository;
        this.eventPublisher = eventPublisher;
        this.fileStorageService = fileStorageService;
        this.transcriptionService = transcriptionService;
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
        MessageResponseDto responseDto = getMessageResponseDto(mensajeGuardado);

        return responseDto;
    }

    @Override
    @Transactional
    public MessageResponseDto enviarMensajeAudio(SendMessageRequestDto requestDto, int autorId) throws Exception {
        User autor = userRepository.findById(autorId)
                .orElseThrow(() -> new Exception("El autor con ID " + autorId + " no existe."));

        Channel canal = channelRepository.findById(requestDto.getChannelId())
                .orElseThrow(() -> new Exception("El canal con ID " + requestDto.getChannelId() + " no existe."));

        // El 'content' del DTO es la ruta temporal del archivo de audio subido por el cliente.
        File audioFile = new File(requestDto.getContent());
        if (!audioFile.exists()) {
            throw new Exception("El archivo de audio no se encuentra en la ruta especificada: " + requestDto.getContent());
        }

        String audioFileName = autorId + "_" + System.currentTimeMillis();
        String storedAudioPath = fileStorageService.storeFile(audioFile, audioFileName, "audio_files");

        // 2. Crear y guardar la entidad AudioMessage en la base de datos.
        AudioMessage nuevoMensaje = new AudioMessage(autor, canal, storedAudioPath);
        AudioMessage mensajeGuardado = (AudioMessage) messageRepository.save(nuevoMensaje);

        // 3. Iniciar la transcripción en un hilo separado para no bloquear la respuesta al cliente.
        String fullAudioPathOnServer = new File(storedAudioPath).getAbsolutePath();
        Executors.newSingleThreadExecutor().submit(() -> {
            transcriptionService.transcribeAndSave(mensajeGuardado, fullAudioPathOnServer);
        });

        MessageResponseDto responseDto = getMessageResponseDto(mensajeGuardado);

        return responseDto;
    }

    private MessageResponseDto getMessageResponseDto(Message mensajeGuardado) {
        MessageResponseDto responseDto = mapToMessageResponseDto(mensajeGuardado);
        List<Integer> memberIds = membresiaCanalRepository.findAllByCanal_ChannelIdAndEstado(responseDto.getChannelId(), EstadoMembresia.ACTIVO)
                .stream()
                .map(membresia -> membresia.getUsuario().getUserId())
                .collect(Collectors.toList());
        eventPublisher.publishEvent(new NewMessageEvent(this, responseDto, memberIds));
        return responseDto;
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
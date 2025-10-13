package com.arquitectura.logica;

import com.arquitectura.DTO.Mensajes.SendMessageRequestDto;
import com.arquitectura.domain.*;
import com.arquitectura.persistence.ChannelRepository;
import com.arquitectura.persistence.MessageRepository;
import com.arquitectura.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageServiceImpl implements IMessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    @Transactional
    public Message enviarMensajeTexto(SendMessageRequestDto requestDto, int autorId) throws Exception {
        User autor = userRepository.findById(autorId)
                .orElseThrow(() -> new Exception("El autor con ID " + autorId + " no existe."));
        
        Channel canal = channelRepository.findById(requestDto.getChannelId())
                .orElseThrow(() -> new Exception("El canal con ID " + requestDto.getChannelId() + " no existe."));

        TextMessage nuevoMensaje = new TextMessage(autor, canal, requestDto.getContent());

        return messageRepository.save(nuevoMensaje);
    }

    @Override
    @Transactional
    public Message enviarMensajeAudio(SendMessageRequestDto requestDto, int autorId) throws Exception {
        User autor = userRepository.findById(autorId)
                .orElseThrow(() -> new Exception("El autor con ID " + autorId + " no existe."));
        
        Channel canal = channelRepository.findById(requestDto.getChannelId())
                .orElseThrow(() -> new Exception("El canal con ID " + requestDto.getChannelId() + " no existe."));

        AudioMessage nuevoMensaje = new AudioMessage(autor, canal, requestDto.getContent());

        return messageRepository.save(nuevoMensaje);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> obtenerMensajesPorCanal(int canalId) {
        return messageRepository.findByChannelChannelId(canalId);
    }
}
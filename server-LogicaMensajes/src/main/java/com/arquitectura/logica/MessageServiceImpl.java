package com.arquitectura.logica;

import com.arquitectura.domain.Channel;
import com.arquitectura.domain.Message;
import com.arquitectura.domain.TextMessage;
import com.arquitectura.domain.AudioMessage;
import com.arquitectura.domain.User;
import com.arquitectura.persistence.ChannelRepository;
import com.arquitectura.persistence.MessageRepository;
import com.arquitectura.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Marca esta clase como un servicio de Spring.
public class MessageServiceImpl implements IMessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    // Inyectamos todos los repositorios que necesitamos.
    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    @Transactional
    public Message enviarMensajeTexto(String contenido, int autorId, int canalId) throws Exception {
        // Buscamos las entidades de autor y canal para asegurar que existen.
        User autor = userRepository.findById(autorId)
                .orElseThrow(() -> new Exception("El autor con ID " + autorId + " no existe."));
        
        Channel canal = channelRepository.findById(canalId)
                .orElseThrow(() -> new Exception("El canal con ID " + canalId + " no existe."));

        // Creamos la nueva entidad de mensaje de texto.
        TextMessage nuevoMensaje = new TextMessage(autor, canal, contenido);

        // Guardamos el mensaje usando el repositorio.
        return messageRepository.save(nuevoMensaje);
    }

    @Override
    @Transactional
    public Message enviarMensajeAudio(String urlAudio, int autorId, int canalId) throws Exception {
        // La lógica es idéntica, solo cambia el tipo de mensaje.
        User autor = userRepository.findById(autorId)
                .orElseThrow(() -> new Exception("El autor con ID " + autorId + " no existe."));
        
        Channel canal = channelRepository.findById(canalId)
                .orElseThrow(() -> new Exception("El canal con ID " + canalId + " no existe."));

        AudioMessage nuevoMensaje = new AudioMessage(autor, canal, urlAudio);

        return messageRepository.save(nuevoMensaje);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> obtenerMensajesPorCanal(int canalId) {
        // ¡CORRECCIÓN APLICADA AQUÍ!
        // Usamos el nombre de método correcto definido en MessageRepository.
        return messageRepository.findByChannelChannelId(canalId);
    }
}
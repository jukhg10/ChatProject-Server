package com.arquitectura.logica;

import com.arquitectura.domain.Channel;
import com.arquitectura.domain.User;
import com.arquitectura.domain.enums.TipoCanal; // <-- IMPORT THE NEW ENUM
import com.arquitectura.persistence.ChannelRepository;
import com.arquitectura.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ChannelServiceImpl implements IChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChannelServiceImpl(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Channel crearCanal(String channelName, User owner) {
        // FIX: Use the new constructor with a default channel type
        Channel newChannel = new Channel(channelName, owner, TipoCanal.GRUPO);
        return channelRepository.save(newChannel);
    }
    
    // ... rest of the file
    @Override
    @Transactional
    public Channel agregarMiembro(int channelId, int userId) throws Exception {
        // Buscamos el canal y el usuario en la base de datos.
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new Exception("El canal con ID " + channelId + " no existe."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("El usuario con ID " + userId + " no existe."));

        // Usamos el método de utilidad que creamos en la clase Channel para añadir el miembro.
        channel.addMember(user);

        // Guardamos el canal actualizado.
        return channelRepository.save(channel);
    }

    @Override
    @Transactional(readOnly = true) // 'readOnly = true' optimiza las consultas que solo leen datos.
    public List<Channel> obtenerTodosLosCanales() {
        return channelRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Channel> findById(int channelId) {
        return channelRepository.findById(channelId);
    }
}
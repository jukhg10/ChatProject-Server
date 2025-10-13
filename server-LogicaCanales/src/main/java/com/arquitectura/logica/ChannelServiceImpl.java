package com.arquitectura.logica;

import com.arquitectura.DTO.canales.CreateChannelRequestDto; // <-- IMPORT AÑADIDO
import com.arquitectura.domain.Channel;
import com.arquitectura.domain.User;
import com.arquitectura.domain.enums.TipoCanal; // <-- IMPORT AÑADIDO
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
    public Channel crearCanal(CreateChannelRequestDto requestDto, User owner) {
        // Se extraen los datos del DTO
        TipoCanal tipo = TipoCanal.valueOf(requestDto.getChannelType().toUpperCase());
        Channel newChannel = new Channel(requestDto.getChannelName(), owner, tipo);
        return channelRepository.save(newChannel);
    }
    
    @Override
    @Transactional
    public Channel agregarMiembro(int channelId, int userId) throws Exception {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new Exception("El canal con ID " + channelId + " no existe."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("El usuario con ID " + userId + " no existe."));

        channel.addMember(user);
        return channelRepository.save(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Channel> obtenerTodosLosCanales() {
        return channelRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Channel> findById(int channelId) {
        return channelRepository.findById(channelId);
    }
}
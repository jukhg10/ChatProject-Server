package com.arquitectura.logica;

import com.arquitectura.DTO.canales.CreateChannelRequestDto; // <-- IMPORT AÑADIDO
import com.arquitectura.DTO.canales.InviteMemberRequestDto;
import com.arquitectura.DTO.canales.RespondToInviteRequestDto;
import com.arquitectura.domain.Channel;
import com.arquitectura.domain.MembresiaCanal;
import com.arquitectura.domain.MembresiaCanalId;
import com.arquitectura.domain.User;
import com.arquitectura.domain.enums.EstadoMembresia;
import com.arquitectura.domain.enums.TipoCanal; // <-- IMPORT AÑADIDO
import com.arquitectura.persistence.ChannelRepository;
import com.arquitectura.persistence.MembresiaCanalRepository;
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
    private final MembresiaCanalRepository membresiaCanalRepository;

    @Autowired
    public ChannelServiceImpl(ChannelRepository channelRepository, UserRepository userRepository, MembresiaCanalRepository membresiaCanalRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.membresiaCanalRepository = membresiaCanalRepository;
    }

    @Override
    @Transactional
    public Channel crearCanal(CreateChannelRequestDto requestDto, int idOwner) throws Exception {
        User owner = userRepository.findById(idOwner)
                .orElseThrow(() -> new Exception("El usuario con ID " + idOwner + " no existe."));
        // Se extraen los datos del DTO
        TipoCanal tipo = TipoCanal.valueOf(requestDto.getChannelType().toUpperCase());
        if (tipo == TipoCanal.DIRECTO) {
            throw new Exception("Los canales directos deben crearse con el método crearCanalDirecto.");
        }
        Channel newChannel = new Channel(requestDto.getChannelName(), owner, tipo);
        //Creador se agrega automáticamente como miembro
        MembresiaCanal membresiaInicial = new MembresiaCanal(
            new MembresiaCanalId(newChannel.getChannelId(), idOwner),
                owner,
                newChannel,
                EstadoMembresia.ACTIVO
        );
        newChannel.getMembresias().add(membresiaInicial);
        return channelRepository.save(newChannel);
    }
    @Override
    @Transactional
    public Channel crearCanalDirecto(int user1Id, int user2Id) throws Exception {
        if (user1Id == user2Id) {
            throw new Exception("No se puede crear un canal directo con uno mismo.");
        }
        //evitar duplicados
        // 1. Buscamos en ambas direcciones (A->B y B->A) por si ya existe.
        Optional<Channel> existingChannel = channelRepository.findDirectChannelBetweenUsers(TipoCanal.DIRECTO, user1Id, user2Id);
        if (existingChannel.isPresent()) {
            System.out.println("Canal directo ya existe entre " + user1Id + " y " + user2Id + ". Devolviendo existente.");
            return existingChannel.get();
        }
        // Hacemos la búsqueda inversa por si se creó al revés
        existingChannel = channelRepository.findDirectChannelBetweenUsers(TipoCanal.DIRECTO, user2Id, user1Id);
        if (existingChannel.isPresent()) {
            System.out.println("Canal directo ya existe entre " + user2Id + " y " + user1Id + ". Devolviendo existente.");
            return existingChannel.get();
        }
        // Si no existe, procedemos a crear uno nuevo
        User user1 = userRepository.findById(user1Id).orElseThrow(() -> new Exception("El usuario con ID " + user1Id + " no existe."));
        User user2 = userRepository.findById(user2Id).orElseThrow(() -> new Exception("El usuario con ID " + user2Id + " no existe."));
        String channelName = "Directo: " + user1.getUsername() + " - " + user2.getUsername();
        Channel directChannel = new Channel(channelName, user1, TipoCanal.DIRECTO); // user1 es el "owner" simbólico
        //guardamos el canal
        Channel savedChannel = channelRepository.save(directChannel);
        // Añadimos a ambos usuarios como miembros activos
        anadirMiembroConEstado(savedChannel, user1, EstadoMembresia.ACTIVO);
        anadirMiembroConEstado(savedChannel, user2, EstadoMembresia.ACTIVO);
        // Volvemos a guardar para persistir las nuevas membresías
        return savedChannel;
    }
    
    @Override
    @Transactional
    public MembresiaCanal invitarMiembro(InviteMemberRequestDto inviteMemberRequestDto,int ownerId) throws Exception {
        Channel channel = channelRepository.findById(inviteMemberRequestDto.getChannelId())
                .orElseThrow(() -> new Exception("Canal no encontrado."));

        if (channel.getOwner().getUserId() != ownerId) {
            throw new Exception("Solo el propietario del canal puede enviar invitaciones.");
        }

        if (channel.getTipo() != TipoCanal.GRUPO) {
            throw new Exception("Solo se pueden enviar invitaciones a canales de tipo GRUPO.");
        }

        User userToInvite = userRepository.findById(inviteMemberRequestDto.getUserIdToInvite())
                .orElseThrow(() -> new Exception("Usuario a invitar no encontrado."));

        MembresiaCanalId membresiaId = new MembresiaCanalId(channel.getChannelId(), userToInvite.getUserId());

        // Verificar si ya existe una membresía
        if(membresiaCanalRepository.existsById(membresiaId)){
            throw new Exception("El usuario ya es miembro o tiene una invitación pendiente.");
        }

        MembresiaCanal nuevaInvitacion = new MembresiaCanal(membresiaId, userToInvite, channel, EstadoMembresia.PENDIENTE);
        return membresiaCanalRepository.save(nuevaInvitacion);
    }

    @Override
    @Transactional
    public MembresiaCanal responderInvitacion(RespondToInviteRequestDto requestDto, int userId) throws Exception {
        MembresiaCanalId membresiaId = new MembresiaCanalId(requestDto.getChannelId(), userId);

        MembresiaCanal invitacion = membresiaCanalRepository.findById(membresiaId)
                .orElseThrow(() -> new Exception("No se encontró una invitación para este usuario en este canal."));

        if (invitacion.getEstado() != EstadoMembresia.PENDIENTE) {
            throw new Exception("No hay una invitación pendiente que responder.");
        }

        if (requestDto.isAccepted()) {
            invitacion.setEstado(EstadoMembresia.ACTIVO);
            return membresiaCanalRepository.save(invitacion);
        } else {
            membresiaCanalRepository.delete(invitacion);
            return null;
        }
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

    private void anadirMiembroConEstado(Channel channel, User user, EstadoMembresia estado) {
        MembresiaCanalId membresiaId = new MembresiaCanalId(channel.getChannelId(), user.getUserId());
        MembresiaCanal nuevaMembresia = new MembresiaCanal(membresiaId, user, channel, estado);
        membresiaCanalRepository.save(nuevaMembresia);
    }
}
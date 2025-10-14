package com.arquitectura.logica;

import com.arquitectura.DTO.canales.ChannelResponseDto;
import com.arquitectura.DTO.canales.CreateChannelRequestDto;
import com.arquitectura.DTO.canales.InviteMemberRequestDto;
import com.arquitectura.DTO.canales.RespondToInviteRequestDto;
import com.arquitectura.domain.Channel;
import com.arquitectura.domain.MembresiaCanal;

import java.util.List;
import java.util.Optional;

public interface IChannelService {

    /**
     * Crea un nuevo canal en el sistema.
     * @param requestDto El DTO con la información del canal a crear.
     * @param idOwner El usuario que crea el canal (dueño).
     * @return El canal recién creado y guardado.
     */

    ChannelResponseDto crearCanal(CreateChannelRequestDto requestDto, int idOwner) throws Exception;

    //chats 1 a 1
    ChannelResponseDto crearCanalDirecto(int user1Id, int user2Id) throws Exception;

    /**
     * Procesa una invitación de un propietario de canal a un nuevo miembro.
     * @param requestDto DTO con el ID del canal y el ID del usuario a invitar.
     * @param ownerId El ID del usuario que realiza la invitación (para validación).
     * @return La nueva membresía en estado PENDIENTE.
     */
    void invitarMiembro(InviteMemberRequestDto requestDto, int ownerId) throws Exception;

    /**
     * Procesa la respuesta de un usuario a una invitación de canal.
     * @param requestDto DTO con el ID del canal y la decisión (aceptar/rechazar).
     * @param userId El ID del usuario que está respondiendo.
     * @return La membresía actualizada si se aceptó, o null si se rechazó.
     */
    void responderInvitacion(RespondToInviteRequestDto requestDto, int userId) throws Exception;


    /**
     * Obtiene todos los canales disponibles.
     * @return Una lista de todos los canales.
     */
    List<ChannelResponseDto> obtenerTodosLosCanales();

    /**
     * Busca un canal por su ID.
     * @param channelId El ID del canal.
     * @return Un Optional que contiene el canal si se encuentra.
     */
    Optional<ChannelResponseDto> findById(int channelId);
}
package com.arquitectura.logica;

import com.arquitectura.DTO.canales.CreateChannelRequestDto;
import com.arquitectura.domain.Channel;
import com.arquitectura.domain.User;
import java.util.List;
import java.util.Optional;

public interface IChannelService {

    /**
     * Crea un nuevo canal en el sistema.
     * @param requestDto El DTO con la información del canal a crear.
     * @param owner El usuario que crea el canal (dueño).
     * @return El canal recién creado y guardado.
     */
    // La declaración del método debe terminar con un punto y coma.
    Channel crearCanal(CreateChannelRequestDto requestDto, User owner); // <-- PUNTO Y COMA AÑADIDO

    /**
     * Agrega un usuario como miembro a un canal existente.
     * @param channelId El ID del canal.
     * @param userId El ID del usuario a agregar.
     * @return El canal actualizado con el nuevo miembro.
     * @throws Exception si el canal o el usuario no existen, o si el usuario ya es miembro.
     */
    Channel agregarMiembro(int channelId, int userId) throws Exception;

    /**
     * Obtiene todos los canales disponibles.
     * @return Una lista de todos los canales.
     */
    List<Channel> obtenerTodosLosCanales();

    /**
     * Busca un canal por su ID.
     * @param channelId El ID del canal.
     * @return Un Optional que contiene el canal si se encuentra.
     */
    Optional<Channel> findById(int channelId);
}
package com.arquitectura.logica;

import com.arquitectura.domain.Channel;
import com.arquitectura.domain.User;
import com.arquitectura.domain.enums.TipoCanal; // Importamos el Enum
import java.util.List;
import java.util.Optional;

public interface IChannelService {

    /**
     * Crea un nuevo canal en el sistema.
     * @param channelName El nombre del nuevo canal.
     * @param owner El usuario que crea el canal (dueño).
     * @param tipo El tipo de canal a crear (GRUPO, DIRECTO, etc.).
     * @return El canal recién creado y guardado.
     */
    Channel crearCanal(String channelName, User owner, TipoCanal tipo); // <-- PARÁMETRO AÑADIDO

    /**
     * Agrega un usuario como miembro a un canal existente.
     * @param channelId El ID del canal.
     * @param userId El ID del usuario a agregar.
     * @return El canal actualizado con el nuevo miembro.
     * @throws Exception si el canal o el usuario no existen.
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
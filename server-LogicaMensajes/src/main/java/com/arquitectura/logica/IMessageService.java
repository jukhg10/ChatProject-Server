package com.arquitectura.logica;

import com.arquitectura.domain.Message;
import java.util.List;

public interface IMessageService {

    /**
     * Guarda un nuevo mensaje de texto en un canal.
     * @param contenido El texto del mensaje.
     * @param autorId El ID del usuario que envía el mensaje.
     * @param canalId El ID del canal donde se envía el mensaje.
     * @return El mensaje de texto guardado.
     * @throws Exception si el autor o el canal no existen.
     */
    Message enviarMensajeTexto(String contenido, int autorId, int canalId) throws Exception;

    /**
     * Guarda un nuevo mensaje de audio en un canal.
     * @param urlAudio La URL o ruta del archivo de audio.
     * @param autorId El ID del usuario que envía el mensaje.
     * @param canalId El ID del canal donde se envía el mensaje.
     * @return El mensaje de audio guardado.
     * @throws Exception si el autor o el canal no existen.
     */
    Message enviarMensajeAudio(String urlAudio, int autorId, int canalId) throws Exception;

    /**
     * Obtiene el historial de mensajes de un canal específico.
     * @param canalId El ID del canal.
     * @return Una lista con los mensajes del canal.
     */
    List<Message> obtenerMensajesPorCanal(int canalId);
}
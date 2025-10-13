package com.arquitectura.fachada;

import com.arquitectura.domain.Channel;
import com.arquitectura.domain.Message;
import com.arquitectura.domain.User;

import java.util.List;
import java.util.Optional;

public interface IChatFachada {

    // --- Métodos de Usuario ---

    User registrarUsuario(String username, String email, String password, String ipAddress) throws Exception;

    Optional<User> buscarUsuarioPorUsername(String username);

    List<User> obtenerTodosLosUsuarios();

    // --- Métodos de Canal ---

    Channel crearCanal(String channelName, User owner);

    Channel agregarMiembroACanal(int channelId, int userId) throws Exception;

    List<Channel> obtenerTodosLosCanales();

    // --- Métodos de Mensaje ---

    Message enviarMensajeTexto(String contenido, int autorId, int canalId) throws Exception;

    Message enviarMensajeAudio(String urlAudio, int autorId, int canalId) throws Exception;

    List<Message> obtenerMensajesDeCanal(int canalId);
}
package com.arquitectura.fachada;

import com.arquitectura.domain.Channel;
import com.arquitectura.domain.Message;
import com.arquitectura.domain.User;
import com.arquitectura.logica.IChannelService;
import com.arquitectura.logica.IMessageService;
import com.arquitectura.logica.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component // Usamos @Component porque es un componente genérico, no un @Service con lógica propia.
public class ChatFachadaImpl implements IChatFachada {

    private final IUserService userService;
    private final IChannelService channelService;
    private final IMessageService messageService;

    // Inyectamos los tres servicios que hemos creado.
    @Autowired
    public ChatFachadaImpl(IUserService userService, IChannelService channelService, IMessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    // --- Métodos de Usuario (Delegación) ---
    @Override
    public User registrarUsuario(String username, String email, String password, String ipAddress) throws Exception {
        return userService.registrarUsuario(username, email, password, ipAddress);
    }

    @Override
    public Optional<User> buscarUsuarioPorUsername(String username) {
        return userService.buscarPorUsername(username);
    }

    // --- Métodos de Canal (Delegación) ---
    @Override
    public Channel crearCanal(String channelName, User owner) {
        return channelService.crearCanal(channelName, owner);
    }

    @Override
    public Channel agregarMiembroACanal(int channelId, int userId) throws Exception {
        return channelService.agregarMiembro(channelId, userId);
    }

    @Override
    public List<Channel> obtenerTodosLosCanales() {
        return channelService.obtenerTodosLosCanales();
    }

    // --- Métodos de Mensaje (Delegación) ---
    @Override
    public Message enviarMensajeTexto(String contenido, int autorId, int canalId) throws Exception {
        return messageService.enviarMensajeTexto(contenido, autorId, canalId);
    }

    @Override
    public Message enviarMensajeAudio(String urlAudio, int autorId, int canalId) throws Exception {
        return messageService.enviarMensajeAudio(urlAudio, autorId, canalId);
    }

    @Override
public List<User> obtenerTodosLosUsuarios() {
    return userService.obtenerTodosLosUsuarios();
}
    @Override
    public List<Message> obtenerMensajesDeCanal(int canalId) {
        return messageService.obtenerMensajesPorCanal(canalId);
    }
}
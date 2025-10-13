package com.arquitectura.controlador;

import com.arquitectura.domain.Channel; // <-- IMPORT AÑADIDO
import com.arquitectura.domain.User;    // <-- IMPORT AÑADIDO
import com.arquitectura.fachada.IChatFachada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List; // <-- IMPORT AÑADIDO

@Controller
public class ServerViewController {

    private final IChatFachada chatFachada;

    @Autowired
    public ServerViewController(IChatFachada chatFachada) {
        this.chatFachada = chatFachada;
    }

    public List<User> obtenerUsuariosRegistrados() {
        // Ahora esta llamada es válida.
        return chatFachada.obtenerTodosLosUsuarios();
    }

    public List<Channel> obtenerCanalesActivos() {
        return chatFachada.obtenerTodosLosCanales();
    }
}
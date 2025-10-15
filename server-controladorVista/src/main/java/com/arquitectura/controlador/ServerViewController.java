package com.arquitectura.controlador;

import com.arquitectura.DTO.canales.ChannelResponseDto;
import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.fachada.IChatFachada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;

@Controller
public class ServerViewController {

    private final IChatFachada chatFachada;

    @Autowired
    public ServerViewController(IChatFachada chatFachada) {
        this.chatFachada = chatFachada;
    }

    // metodos registro y mensaje

    public List<UserResponseDto> obtenerUsuariosRegistrados() {
        return chatFachada.obtenerTodosLosUsuarios();
    }

    public void enviarMensajeBroadcast(String contenido) {
        final int ADMIN_ID = 1; // Asumimos que el admin tiene el userId = 1
        try {
            chatFachada.enviarMensajeBroadcast(contenido, ADMIN_ID);
        } catch (Exception e) {
            e.printStackTrace(); // En una app real, usaríamos un logger
        }
    }

    public void registrarNuevoUsuario(UserRegistrationRequestDto requestDto) throws Exception {
        // La IP para un registro desde el servidor puede ser "localhost"
        chatFachada.registrarUsuario(requestDto, "127.0.0.1");
    }
    // --- NUEVOS MÉTODOS PARA LOS INFORMES ---

    /**
     * NOTA: Este método requiere crear primero la lógica en el backend (Servicio y Fachada).
     * Devolverá un mapa donde la clave es el canal y el valor es la lista de sus miembros.
     */
    public Map<ChannelResponseDto, List<UserResponseDto>> obtenerCanalesConMiembros() {
        // return chatFachada.obtenerCanalesConMiembros(); // Descomentar cuando la fachada lo tenga
        System.out.println("Lógica de backend para 'obtenerCanalesConMiembros' no implementada.");
        return null; // Valor temporal
    }

    /**
     * NOTA: Este método requiere crear primero la lógica en el backend.
     * Devolverá la lista de usuarios actualmente conectados.
     */
    public List<UserResponseDto> obtenerUsuariosConectados() {
        // return chatFachada.obtenerUsuariosConectados(); // Descomentar cuando la fachada lo tenga
        System.out.println("Lógica de backend para 'obtenerUsuariosConectados' no implementada.");
        return null; // Valor temporal
    }

    /**
     * NOTA: Este método requiere crear primero la lógica en el backend.
     * Devolverá las transcripciones de los mensajes de audio.
     */
    public List<String> obtenerTranscripciones() {
        // return chatFachada.obtenerTranscripciones(); // Descomentar cuando la fachada lo tenga
        System.out.println("Lógica de backend para 'obtenerTranscripciones' no implementada.");
        return null; // Valor temporal
    }

}
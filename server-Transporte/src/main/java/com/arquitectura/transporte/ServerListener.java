package com.arquitectura.transporte;

import com.arquitectura.DTO.Mensajes.MessageResponseDto;
import com.arquitectura.controlador.IClientHandler;
import com.arquitectura.controlador.RequestDispatcher;
import com.arquitectura.events.BroadcastMessageEvent;
import com.arquitectura.events.NewMessageEvent;
import com.arquitectura.events.UserInvitedEvent;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@PropertySource("file:./config/server.properties")
@Component
public class ServerListener {
    @Value("${server.port}")
    private int port;
    @Value("${server.max.connections}")
    private int maxConnectedUsers;

    private final Gson gson = new Gson();
    private ExecutorService clientPool;
    private final RequestDispatcher requestDispatcher;
    private static final Logger log = LoggerFactory.getLogger(ServerListener.class);
    private final Map<Integer, List<IClientHandler>> activeClientsById = Collections.synchronizedMap(new HashMap<>());
    @Autowired
    public ServerListener(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }
    @PostConstruct
    public void init() {
        this.clientPool = Executors.newFixedThreadPool(maxConnectedUsers);
    }
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Servidor de Chat iniciado en el puerto {} con un límite de {} conexiones.", port, maxConnectedUsers);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                // La lógica de rechazo ahora cuenta el total de conexiones, no solo usuarios únicos.
                int totalConnections = activeClientsById.values().stream().mapToInt(List::size).sum();
                if (totalConnections >= maxConnectedUsers) {
                    log.warn("Conexión rechazada de {}. Límite de {} conexiones alcanzado.", clientSocket.getInetAddress().getHostAddress(), maxConnectedUsers);
                    try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                        out.println("ERROR;SERVER_FULL;El servidor ha alcanzado su capacidad máxima.");
                    }
                    clientSocket.close();
                    continue;
                }

                log.info("Nuevo cliente conectado: {}", clientSocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, requestDispatcher, this::removeClient);
                clientPool.submit(clientHandler);
            }
        } catch (IOException e) {
            log.error("Error fatal al iniciar el servidor: {}", e.getMessage(), e);
        }
    }
    /**
            * Escucha los eventos de broadcast publicados por la capa de lógica.
     * @param event El evento que contiene el mensaje a difundir.
     */
    @EventListener
    public void handleBroadcastEvent(BroadcastMessageEvent event) {
        log.info("Evento de Broadcast recibido. Enviando a todas las sesiones activas.");
        activeClientsById.values().forEach(handlerList -> {
            handlerList.forEach(handler -> handler.sendMessage(event.getFormattedMessage()));
        });
    }
    @EventListener
    public void handleNewMessageEvent(NewMessageEvent event) {
        MessageResponseDto messageDto = event.getMessageDto();
        log.info("Nuevo mensaje en canal {}. Propagando a los miembros conectados.", messageDto.getChannelId());

        // 1. Obtener la lista de destinatarios DIRECTAMENTE del evento.
        List<Integer> memberIds = event.getRecipientUserIds();

        // 2. Preparar la notificación una sola vez.
        String notification = "EVENTO;NUEVO_MENSAJE;" + gson.toJson(messageDto);

        // 3. Enviar la notificación a cada miembro conectado (la lógica no cambia).
        memberIds.forEach(memberId -> {
            List<IClientHandler> userSessions = activeClientsById.get(memberId);
            if (userSessions != null) {
                userSessions.forEach(handler -> handler.sendMessage(notification));
            }
        });
    }
    @EventListener
    public void handleUserInvitedEvent(UserInvitedEvent event) {
        int invitedUserId = event.getInvitedUserId();
        log.info("Usuario {} invitado al canal '{}'. Notificando si está en línea.", invitedUserId, event.getChannelDto().getChannelName());

        List<IClientHandler> userSessions = activeClientsById.get(invitedUserId);

        if (userSessions != null && !userSessions.isEmpty()) {
            String notificationJson = gson.toJson(event.getChannelDto());
            String notification = "EVENTO;NUEVA_INVITACION;" + notificationJson;

            userSessions.forEach(handler -> handler.sendMessage(notification));
            log.info("Notificación de invitación enviada al usuario {}.", invitedUserId);
        }
    }


    //metodos publicos para gestion de sesiones
    public Set<Integer> getConnectedUserIds() {
        // Devuelve una copia para evitar modificaciones accidentales
        return new HashSet<>(activeClientsById.keySet());
    }

    public void registerAuthenticatedClient(int userId, IClientHandler handler) {
        // computeIfAbsent es una forma segura de obtener la lista o crearla si no existe.
        List<IClientHandler> userHandlers = activeClientsById.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());
        userHandlers.add(handler);
        log.info("Nueva sesión registrada para usuario {}. Total de sesiones para este usuario: {}.", userId, userHandlers.size());
    }

    //metodos privados para gestion de sesiones
    private void removeClient(IClientHandler clientHandler) {
        if (clientHandler.isAuthenticated()) {
            int userId = clientHandler.getAuthenticatedUser().getUserId();
            List<IClientHandler> userHandlers = activeClientsById.get(userId);
            if (userHandlers != null) {
                userHandlers.remove(clientHandler);
                log.info("Sesión eliminada para usuario {}. Sesiones restantes para este usuario: {}.", userId, userHandlers.size());
                // Si la lista queda vacía, eliminamos la entrada del mapa.
                if (userHandlers.isEmpty()) {
                    activeClientsById.remove(userId);
                    log.info("Usuario {} se ha desconectado por completo.", userId);
                }
            }
        }
        log.info("Cliente [{}] desconectado.", clientHandler.getClientIpAddress());
    }

}
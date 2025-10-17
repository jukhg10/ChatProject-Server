package com.arquitectura.persistence.data;

import com.arquitectura.domain.Channel;
import com.arquitectura.domain.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "messages")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Estrategia para guardar las clases hijas.
@DiscriminatorColumn(name = "message_type")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Long idMensaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel; // <-- ¡CAMBIO IMPORTANTE!

    private LocalDateTime timestamp;

    // ... constructores, getters y setters (necesitamos actualizar el constructor) ...

    public MessageEntity() {
    }

    // Constructor actualizado
    public MessageEntity(User author, Channel channel) {
        this.author = author;
        this.channel = channel;
        this.timestamp = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    // (Añade getters y setters para 'channel')

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    // ... otros getters y setters no cambian ...
    public Long getIdMensaje() { return idMensaje; }
    public void setIdMensaje(Long id) { this.idMensaje = id; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

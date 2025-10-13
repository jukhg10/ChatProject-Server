package com.arquitectura.domain;

import com.arquitectura.domain.enums.TipoCanal;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "channels")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id")
    private int channelId;

    @Column(name = "channel_name",nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) // Relación: Muchos canales pueden tener un mismo dueño.
    @JoinColumn(name = "owner_id", nullable = false) // Clave foránea en la tabla 'channels'.
    private User owner;

    @Column(nullable = false) // ¡AÑADIDO!
    @Enumerated(EnumType.STRING) // Indica a JPA cómo guardar el Enum
    private TipoCanal tipo; // Necesitarás crear un Enum ChannelType { DIRECTO, GRUPO }

    @ManyToMany(fetch = FetchType.LAZY) // Relación: Muchos usuarios en muchos canales.
    @JoinTable(
        name = "channel_members", // Nombre de la tabla intermedia.
        joinColumns = @JoinColumn(name = "channel_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members;

    // ... el resto de la clase (constructores, getters, setters) no cambia ...

       public Channel() {
                this.members = new ArrayList<>();
            }
    // Constructor para crear un nuevo canal
    public Channel(  String name,User owner, TipoCanal tipo) {
        this.name = name;
        this.owner = owner;
        this.tipo = tipo;
        this.members = new ArrayList<>();
        this.members.add(owner);
    }

    // --- Getters y Setters ---

    public TipoCanal getTipo() {
        return tipo;
    }

    public void setTipo(TipoCanal tipo) {
        this.tipo = tipo;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int id) {
        this.channelId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    // --- Métodos de utilidad ---

    public void addMember(User user) {
        if (!this.members.contains(user)) {
            this.members.add(user);
        }
    }

    public void removeMember(User user) {
        this.members.remove(user);
    }
}
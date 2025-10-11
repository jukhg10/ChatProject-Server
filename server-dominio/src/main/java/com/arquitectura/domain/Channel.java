package com.arquitectura.domain;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "channels")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) // Relación: Muchos canales pueden tener un mismo dueño.
    @JoinColumn(name = "owner_id", nullable = false) // Clave foránea en la tabla 'channels'.
    private User owner;

    @ManyToMany(fetch = FetchType.LAZY) // Relación: Muchos usuarios en muchos canales.
    @JoinTable(
        name = "channel_members", // Nombre de la tabla intermedia.
        joinColumns = @JoinColumn(name = "channel_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members;

    // ... el resto de la clase (constructores, getters, setters) no cambia ...

    // Constructor vacío
    public Channel() {
        this.members = new ArrayList<>();
    }

    // Constructor para crear un nuevo canal
    public Channel(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.members = new ArrayList<>();
        this.members.add(owner); // El creador es el primer miembro
    }

    // --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
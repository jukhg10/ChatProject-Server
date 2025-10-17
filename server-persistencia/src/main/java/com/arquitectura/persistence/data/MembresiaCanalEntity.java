//package com.arquitectura.persistence.data;
//
//import com.arquitectura.domain.Channel;
//import com.arquitectura.persistence.data.enums.EstadoMembresia;
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "membresia_canal")
//public class MembresiaCanalEntity {
//
//    @EmbeddedId // Usa la clase de clave compuesta como ID
//    private MembresiaCanalIdEntity id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("IdUsuario") // Mapea el campo 'idUsuario' de nuestra clave compuesta a esta relación
//    @JoinColumn(name = "user_id")
//    private UserEntity usuario;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("IdCanal") // Mapea el campo 'idCanal' de nuestra clave compuesta a esta relación
//    @JoinColumn(name = "channel_id")
//    private Channel canal;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private EstadoMembresia estado;
//
//    public MembresiaCanalEntity() {
//    }
//
//    public MembresiaCanalEntity(MembresiaCanalIdEntity id, UserEntity usuario, Channel canal, EstadoMembresia estado) {
//        this.id = id;
//        this.usuario = usuario;
//        this.canal = canal;
//        this.estado = estado;
//    }
//
//    public MembresiaCanalIdEntity getId() {
//        return id;
//    }
//
//    public void setId(MembresiaCanalIdEntity id) {
//        this.id = id;
//    }
//
//    public UserEntity getUsuario() {
//        return usuario;
//    }
//
//    public void setUsuario(UserEntity usuario) {
//        this.usuario = usuario;
//    }
//
//    public Channel getCanal() {
//        return canal;
//    }
//
//    public void setCanal(Channel canal) {
//        this.canal = canal;
//    }
//
//    public EstadoMembresia getEstado() {
//        return estado;
//    }
//
//    public void setEstado(EstadoMembresia estado) {
//        this.estado = estado;
//    }
//}

package com.arquitectura.persistence;

import com.arquitectura.domain.MembresiaCanal;
import com.arquitectura.domain.MembresiaCanalId;
import com.arquitectura.domain.enums.EstadoMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembresiaCanalRepository extends JpaRepository<MembresiaCanal, MembresiaCanalId> {
    List<MembresiaCanal> findAllByUsuarioUserIdAndEstado(int userId, EstadoMembresia estado);
    List<MembresiaCanal> findAllByCanal_ChannelIdAndEstado(int channelId, EstadoMembresia estado);
}
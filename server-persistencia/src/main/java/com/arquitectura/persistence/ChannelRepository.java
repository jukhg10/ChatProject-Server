package com.arquitectura.persistence;

import com.arquitectura.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Integer> {
    // JpaRepository ya nos da todos los métodos que necesitamos por ahora.
    // Podemos añadir métodos de búsqueda personalizados aquí en el futuro si es necesario.
}
package com.arquitectura.persistence;

import com.arquitectura.domain.MembresiaCanal;
import com.arquitectura.domain.MembresiaCanalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembresiaCanalRepository extends JpaRepository<MembresiaCanal, MembresiaCanalId> {
}
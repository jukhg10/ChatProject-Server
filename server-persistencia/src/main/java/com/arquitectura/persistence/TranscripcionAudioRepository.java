package com.arquitectura.persistence;

import com.arquitectura.domain.TranscripcionAudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranscripcionAudioRepository extends JpaRepository<TranscripcionAudio, Long> {
}
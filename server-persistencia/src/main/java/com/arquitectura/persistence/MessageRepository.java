package com.arquitectura.persistence;

import com.arquitectura.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
// The primary key of Message is Long, so this should be <Message, Long>
public interface MessageRepository extends JpaRepository<Message, Long> {

    // CORRECTED METHOD NAME:
    // This now tells Spring to look for message.channel.channelId
    List<Message> findByChannelChannelId(int channelId);
}
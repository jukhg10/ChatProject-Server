package com.arquitectura.persistence.data;

import com.arquitectura.domain.Channel;
import com.arquitectura.domain.Message;
import com.arquitectura.domain.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TEXT")
public class TextMessageEntity extends MessageEntity {
    private String content;

    public TextMessageEntity() {
        super();
    }

    // Constructor actualizado
    public TextMessageEntity(User author, Channel channel, String content) {
        super(author, channel);
        this.content = content;
    }

    // ... getters y setters no cambian ...
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}

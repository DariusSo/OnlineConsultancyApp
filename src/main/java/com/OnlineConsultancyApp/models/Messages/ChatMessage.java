package com.OnlineConsultancyApp.models.Messages;

import com.OnlineConsultancyApp.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatMessage extends Message{
    private String name;
    private LocalDateTime sentAt;
    private MessageType messageType;

    public ChatMessage(String name, String message, LocalDateTime sentAt, MessageType messageType) {
        super(message);
        this.name = name;
        this.sentAt = sentAt;
        this.messageType = messageType;
    }
}

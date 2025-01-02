package com.OnlineConsultancyApp.models.Messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmailMessage extends Message{
    private String receiver;

    public EmailMessage(String receiver, String message) {
        super(message);
        this.receiver = receiver;
    }
}

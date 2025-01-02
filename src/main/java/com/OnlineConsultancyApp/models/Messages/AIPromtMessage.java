package com.OnlineConsultancyApp.models.Messages;

import com.OnlineConsultancyApp.enums.Roles;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIPromtMessage extends Message{

    private Roles role;
    private int promptQuantity;

    public AIPromtMessage(String message, Roles role, int promptQuantity) {
        super(message);
        this.role = role;
        this.promptQuantity = promptQuantity;
    }
}

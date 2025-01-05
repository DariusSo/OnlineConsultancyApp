package com.OnlineConsultancyApp.models.Messages;

import com.OnlineConsultancyApp.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ForumMessage {
    private long id;
    private String name;
    private String question;
    private String answer;
    private long consultantId;


}

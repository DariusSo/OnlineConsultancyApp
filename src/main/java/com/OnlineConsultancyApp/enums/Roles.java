package com.OnlineConsultancyApp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Roles {
    CLIENT,
    CONSULTANT,
    USER,
    ASSISTANT,
    SYSTEM;

    @JsonCreator
    public static Categories fromString(String value) {
        return Categories.valueOf(value.toUpperCase());
    }
}

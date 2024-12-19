package com.OnlineConsultancyApp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Categories {

    FINANCIAL,
    LEGAL,
    IT,
    CAREER,
    HEALTH,
    MARKETING,
    BUSINESS,
    OTHER;


    @JsonCreator
    public static Categories fromString(String value) {
        return Categories.valueOf(value.toUpperCase());
    }
}

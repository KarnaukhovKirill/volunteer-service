package ru.kirill.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ContactInfoDto {
    private final long volunteerId;
    private final String contact;
    private final CONTACTTYPE contactType;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;

    public enum CONTACTTYPE {
        PHONE, EMAIL
    }
}

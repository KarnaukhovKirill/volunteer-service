package ru.kirill.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VolunteerListRequest {
    private final String city;
    private final String restrict;
    private final STATUS status;
}

package ru.kirill.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class VolunteerInfosResponse {
    private final List<VolunteerInfo> volunteers;
}

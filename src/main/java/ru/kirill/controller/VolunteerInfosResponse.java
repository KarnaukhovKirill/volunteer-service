package ru.kirill.controller;

import lombok.Data;

import java.util.List;

@Data
public class VolunteerInfosResponse {
    private final List<VolunteerInfo> volunteers;
}

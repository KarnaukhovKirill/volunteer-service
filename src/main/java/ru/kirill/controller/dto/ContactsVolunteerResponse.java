package ru.kirill.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContactsVolunteerResponse {
    private final List<VolunteerContact> volunteerContacts;
}

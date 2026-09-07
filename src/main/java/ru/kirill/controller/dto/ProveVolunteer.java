package ru.kirill.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProveVolunteer {
    @Positive(message = "Номер инцидента должен быть больше 0")
    private final long incNumber;
    @NotNull(message = "Действие не должно быть null")
    private final ACTION action;
}

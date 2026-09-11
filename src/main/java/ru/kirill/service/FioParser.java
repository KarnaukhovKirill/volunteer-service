package ru.kirill.service;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class FioParser {

    @Named("extractFirstName")
    public String extractFirstName(String fio) {
        if (fio == null || fio.trim().isEmpty()) {
            return null;
        }
        String[] parts = fio.trim().split("\\s+");
        return parts.length >= 2 ? parts[1] : null;
    }

    @Named("extractLastName")
    public String extractLastName(String fio) {
        if (fio == null || fio.trim().isEmpty()) {
            return null;
        }
        String[] parts = fio.trim().split("\\s+");
        return parts.length >= 1 ? parts[0] : null;
    }

    @Named("extractMiddleName")
    public String extractMiddleName(String fio) {
        if (fio == null || fio.trim().isEmpty()) {
            return null;
        }
        String[] parts = fio.trim().split("\\s+");
        return parts.length >= 3 ? parts[2] : null;
    }
}

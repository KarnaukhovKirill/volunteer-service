package ru.kirill.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.kirill.Base;
import ru.kirill.BaseIntegrationTest;
import org.junit.jupiter.api.*;
import ru.kirill.controller.dto.VolunteerInfo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

//todo service tests
@AutoConfigureMockMvc
@SpringBootTest
public class VolunteerServiceTest extends BaseIntegrationTest {

    @Autowired
    private VolunteerService volunteerService;
    @Mock
    private VolunteerRepository volunteerRepository;

    @Test
    public void findByName_success() {
        VolunteerInfo volunteerInfo = new VolunteerInfo(1);
        when(volunteerRepository.findByName(Base.USERNAME)).thenReturn(Optional.of(volunteerInfo));
        var volunteer = volunteerService.findByName(Base.USERNAME);
        assertEquals(volunteer.getFirstName(), "Username1");
    }
}

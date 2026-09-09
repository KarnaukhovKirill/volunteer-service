package ru.kirill.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.kirill.Base;
import ru.kirill.BaseIntegrationTest;
import ru.kirill.controller.dto.*;
import ru.kirill.controller.exception.VolunteerServiceException;
import ru.kirill.storage.VolunteerInfo;
import ru.kirill.storage.VolunteerRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
public class VolunteerServiceTest extends BaseIntegrationTest {

    @Autowired
    private VolunteerService volunteerService;
    @MockitoBean
    private VolunteerRepository volunteerRepository;

    @Test
    public void findByName_success() {
        VolunteerInfo volunteerInfo = VolunteerInfo.builder().firstName("Username1").build();
        when(volunteerRepository.findByName(Base.USERNAME)).thenReturn(Optional.of(volunteerInfo));
        var volunteer = volunteerService.get(Base.USERNAME);
        assertEquals("Username1", volunteer.getFirstName());
    }

    @Test
    public void createTest_success() {
        CreateVolunteerRequest request = CreateVolunteerRequest.builder()
                .fio("Степанов Владимир Сергеевич")
                .phoneNumber("79061233564")
                .gender(GENDER.MALE)
                .email("yaVladimir@mail.com")
                .birthday(LocalDate.of(1999, 2, 20))
                .city("Moscow")
                .build();
        VolunteerInfoDto volunteerInfoDto = volunteerService.create(request, "Stepanov01");
        assertThat(volunteerInfoDto.getId()).isGreaterThanOrEqualTo(0);
    }

    public void createTest_conflict() {
        //todo test when already volunteer exists
    }

    @Test
    public void getTest_success_notFoundVolunteer() {
        when(volunteerRepository.findByName(Base.NOT_EXIST_USERNAME)).thenThrow(new VolunteerServiceException("Пользователя с таким именем не существует", HttpStatus.BAD_REQUEST));
        assertThrows(
                VolunteerServiceException.class,
                () -> volunteerService.get(Base.NOT_EXIST_USERNAME)
        );
    }

    @Test
    public void deleteTest_success() {
        VolunteerInfo volunteerInfo = VolunteerInfo.builder().firstName(Base.USER_FOR_DELETE_TEST).build();
        when(volunteerRepository.findByName(Base.USER_FOR_DELETE_TEST)).thenReturn(Optional.of(volunteerInfo));
        Optional<VolunteerInfo> volunteer = volunteerRepository.findByName(Base.USER_FOR_DELETE_TEST);
        assertThat(volunteer).isNotEmpty();

        volunteerService.delete(Base.USER_FOR_DELETE_TEST);

        when(volunteerRepository.findByName(Base.USER_FOR_DELETE_TEST)).thenReturn(Optional.empty());
        Optional<VolunteerInfo> notExistsVolunteer = volunteerRepository.findByName(Base.USER_FOR_DELETE_TEST);
        assertThat(notExistsVolunteer).isEmpty();
    }

    @Test
    public void updateTest_success() {
        when(volunteerRepository.findByName(Base.USERNAME_FOR_UPDATE))
                .thenReturn(Optional.of(VolunteerInfo.builder().firstName("Пётр").lastName("Васюков").createDate(LocalDateTime.of(2026, 9, 6, 12, 12)).build()));

        Optional<VolunteerInfo> volunteer = volunteerRepository.findByName(Base.USERNAME_FOR_UPDATE);
        assertEquals("Васюков", volunteer.get().getLastName());

        UpdateVolunteerRequest updateRequest = UpdateVolunteerRequest.builder().lastName("Васюткин").build();
        VolunteerInfoDto updated = volunteerService.update(Base.USERNAME_FOR_UPDATE, updateRequest);

        when(volunteerRepository.findByName(Base.USERNAME_FOR_UPDATE))
                .thenReturn(Optional.of(VolunteerInfo.builder().firstName("Пётр").lastName("Васюткин").createDate(LocalDateTime.of(2026, 9, 6, 12, 12)).build()));
        assertEquals("Васюткин", updated.getLastName());
    }

    @Test
    public void proveAction_success() {
        //todo add incidentService for check current action
        ProveVolunteer prove = ProveVolunteer.builder().incNumber(1).action(ACTION.PROVE).build();
        volunteerService.proveAction(prove, "Prover01");
    }

    @Test
    public void getById_success() {
        VolunteerInfo exampleVolunteer = VolunteerInfo.builder().firstName("Кирилл").userId("Kirill01").build();
        when(volunteerRepository.findByName(Base.USERNAME)).thenReturn(Optional.of(exampleVolunteer));
        Optional<VolunteerInfo> volunteer = volunteerRepository.findByName(Base.USERNAME);

        when(volunteerRepository.findByName("Kirill01")).thenReturn(Optional.of(exampleVolunteer));
        VolunteerInfoDto volunteer2 = volunteerService.get(volunteer.get().getUserId());
        assertEquals("Кирилл", volunteer2.getFirstName());
    }

    @Test
    public void getByRequest_success() {

        VolunteerListRequest request = VolunteerListRequest.builder().city("Moscow").status(STATUS.AVAILABLE).build();

        when(volunteerRepository.get(request)).thenReturn(VolunteerInfoDto.builder().id(1003L).build());

        VolunteerInfosResponse response = volunteerService.get(request);

        assertEquals(1003L, response.getVolunteers().get(0).getId());

    }
}

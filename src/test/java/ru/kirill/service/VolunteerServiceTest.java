package ru.kirill.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import ru.kirill.Base;
import ru.kirill.controller.dto.*;
import ru.kirill.controller.exception.VolunteerServiceException;
import ru.kirill.mapper.VolunteerMapperImpl;
import ru.kirill.storage.location.LocationRep;
import ru.kirill.storage.entity.Location;
import ru.kirill.storage.volunteer.VolunteerRep;
import ru.kirill.storage.entity.Volunteer;
import ru.kirill.utils.FioParser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VolunteerServiceTest {

    @Mock
    private VolunteerRep volunteerRepository;
    @Mock
    private ContactService contactService;
    @Mock
    private LocationRep locationRep;

    private VolunteerService volunteerService;

    @BeforeEach
    public void setUp() {
        VolunteerMapperImpl mapper = new VolunteerMapperImpl();
        ReflectionTestUtils.setField(mapper, "fioParser", new FioParser());
        volunteerService = new VolunteerServiceImpl(volunteerRepository, mapper, contactService, locationRep);
    }

    @Test
    public void findByName_success() {
        Volunteer volunteerInfo = Volunteer.builder().firstName("Username1").build();
        when(volunteerRepository.findByUserId(Base.USERNAME)).thenReturn(List.of(volunteerInfo));
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
        Location location = Location.builder().name("Moscow").build();
        when(locationRep.findOrCreate("Moscow")).thenReturn(location);
        when(volunteerRepository.saveAndFlush(any(Volunteer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        VolunteerInfoDto volunteerInfoDto = volunteerService.create(request, Base.STEPANOV01);
        assertThat(volunteerInfoDto.getStatus()).isEqualTo(STATUS.AVAILABLE);
        assertThat(volunteerInfoDto.getFirstName()).isEqualTo("Владимир");
        assertThat(volunteerInfoDto.getLastName()).isEqualTo("Степанов");
        assertThat(volunteerInfoDto.getMiddleName()).isEqualTo("Сергеевич");
        assertThat(volunteerInfoDto.getUserId()).isEqualTo(Base.STEPANOV01);

        ArgumentCaptor<Volunteer> saved = ArgumentCaptor.forClass(Volunteer.class);
        verify(volunteerRepository).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getLocation()).isSameAs(location);
        verify(contactService).create(saved.getValue(), "yaVladimir@mail.com", "79061233564");
    }

    @Test
    public void createTest_withRestrict_locationWithRestrict() {
        CreateVolunteerRequest request = CreateVolunteerRequest.builder()
                .fio("Степанов Владимир Сергеевич")
                .phoneNumber("79061233564")
                .gender(GENDER.MALE)
                .email("yaVladimir@mail.com")
                .birthday(LocalDate.of(1999, 2, 20))
                .city("Moscow")
                .restrict("Khimky")
                .build();
        when(volunteerRepository.saveAndFlush(any(Volunteer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        volunteerService.create(request, Base.STEPANOV01);

        verify(locationRep).findOrCreate("Moscow(Khimky)");
    }

    @Test
    public void createTest_contactConflict() {
        CreateVolunteerRequest request = CreateVolunteerRequest.builder()
                .fio("Степанов Владимир Сергеевич")
                .phoneNumber("79061233564")
                .gender(GENDER.MALE)
                .email("yaVladimir@mail.com")
                .birthday(LocalDate.of(1999, 2, 20))
                .city("Moscow")
                .build();
        when(volunteerRepository.saveAndFlush(any(Volunteer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new DataIntegrityViolationException("constraint")).when(volunteerRepository).flush();

        VolunteerServiceException exception = assertThrows(
                VolunteerServiceException.class,
                () -> volunteerService.create(request, Base.STEPANOV01)
        );
        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatus()
        );
        assertThat(exception.getMessage()).contains("Email or phone");
    }

    @Test
    public void createTest_withoutRestrict_locationIsCity() {
        CreateVolunteerRequest request = CreateVolunteerRequest.builder()
                .fio("Степанов Владимир Сергеевич")
                .phoneNumber("79061233564")
                .gender(GENDER.MALE)
                .email("yaVladimir@mail.com")
                .birthday(LocalDate.of(1999, 2, 20))
                .city("Moscow")
                .build();
        when(volunteerRepository.saveAndFlush(any(Volunteer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        volunteerService.create(request, Base.STEPANOV01);

        verify(locationRep).findOrCreate("Moscow");
    }

    @Test
    public void createTest_contactServiceThrows_conflict() {
        CreateVolunteerRequest request = CreateVolunteerRequest.builder()
                .fio("Степанов Владимир Сергеевич")
                .phoneNumber("79061233564")
                .gender(GENDER.MALE)
                .email("yaVladimir@mail.com")
                .birthday(LocalDate.of(1999, 2, 20))
                .city("Moscow")
                .build();
        when(volunteerRepository.saveAndFlush(any(Volunteer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new DataIntegrityViolationException("constraint"))
                .when(contactService).create(any(Volunteer.class), any(), any());

        VolunteerServiceException ex = assertThrows(
                VolunteerServiceException.class,
                () -> volunteerService.create(request, Base.STEPANOV01)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertThat(ex.getMessage()).contains("Email or phone");
        verify(volunteerRepository, never()).flush();
    }

    @Test
    public void createTest_conflict() {
        CreateVolunteerRequest request = CreateVolunteerRequest.builder()
                .fio("Степанов Владимир Сергеевич")
                .phoneNumber("79061233564")
                .gender(GENDER.MALE)
                .email("yaVladimir@mail.com")
                .birthday(LocalDate.of(1999, 2, 20))
                .city("Moscow")
                .build();
        when(volunteerRepository.saveAndFlush(any(Volunteer.class))).thenThrow(new DataIntegrityViolationException("constraint"));
        VolunteerServiceException exception = assertThrows(
                VolunteerServiceException.class,
                () -> volunteerService.create(request, Base.STEPANOV01)
        );
        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatus()
        );
        assertThat(exception.getMessage()).contains(Base.STEPANOV01);
        verifyNoInteractions(contactService);
    }

    @Test
    public void getTest_notFoundVolunteer() {
        when(volunteerRepository.findByUserId(Base.NOT_EXIST_USERNAME)).thenReturn(List.of());
        assertThrows(
                VolunteerServiceException.class,
                () -> volunteerService.get(Base.NOT_EXIST_USERNAME)
        );
    }

    @Test
    public void deleteTest_success() {
        volunteerService.delete(Base.USER_FOR_DELETE_TEST);

        verify(volunteerRepository).deleteByUserId(Base.USER_FOR_DELETE_TEST);
    }

    @Test
    public void updateTest_success() {
        UUID uuid = UUID.randomUUID();
        UpdateVolunteerRequest updateRequest = UpdateVolunteerRequest.builder().lastName("Васюткин").build();

        when(volunteerRepository.findByUserId(Base.USERNAME_FOR_UPDATE))
                .thenReturn(List.of(Volunteer.builder().id(uuid).firstName("Пётр").lastName("Васюков").createDate(LocalDateTime.of(2026, 9, 6, 12, 12)).build()));
        when(volunteerRepository.findById(uuid))
                .thenReturn(Optional.of(Volunteer.builder().id(uuid).firstName("Пётр").lastName("Васюткин").createDate(LocalDateTime.of(2026, 9, 6, 12, 12)).build()));
        when(volunteerRepository.update(Base.USERNAME_FOR_UPDATE, updateRequest)).thenReturn(1);

        VolunteerInfoDto updated = volunteerService.update(Base.USERNAME_FOR_UPDATE, updateRequest);

        assertEquals("Васюткин", updated.getLastName());

        verify(volunteerRepository).update(any(), any(UpdateVolunteerRequest.class));
        verify(contactService).update(uuid, updateRequest);
    }

    @Test
    public void updateTest_notFoundVolunteer() {
        when(volunteerRepository.update(any(), any(UpdateVolunteerRequest.class))).thenReturn(0);

        UpdateVolunteerRequest updateRequest = UpdateVolunteerRequest.builder().lastName("Васюткин").build();

        VolunteerServiceException exception = assertThrows(
                VolunteerServiceException.class,
                () -> volunteerService.update(Base.USERNAME_FOR_UPDATE, updateRequest)
        );
        assertTrue(
                exception.getStatus().is4xxClientError()
        );
        verify(volunteerRepository, never()).findByUserId(any());
        verifyNoInteractions(contactService);
    }

    @Test
    public void updateTest_volunteerDisappearedAfterUpdate_notFound() {
        UpdateVolunteerRequest updateRequest = UpdateVolunteerRequest.builder().lastName("Васюткин").build();
        when(volunteerRepository.update(Base.USERNAME_FOR_UPDATE, updateRequest)).thenReturn(1);
        when(volunteerRepository.findByUserId(Base.USERNAME_FOR_UPDATE)).thenReturn(List.of());

        VolunteerServiceException ex = assertThrows(
                VolunteerServiceException.class,
                () -> volunteerService.update(Base.USERNAME_FOR_UPDATE, updateRequest)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verifyNoInteractions(contactService);
    }

    @Test
    public void updateTest_contactConflict() {
        UUID uuid = UUID.randomUUID();
        UpdateVolunteerRequest updateRequest = UpdateVolunteerRequest.builder().email("used@mail.com").build();
        when(volunteerRepository.update(Base.USERNAME_FOR_UPDATE, updateRequest)).thenReturn(1);
        when(volunteerRepository.findByUserId(Base.USERNAME_FOR_UPDATE))
                .thenReturn(List.of(Volunteer.builder().id(uuid).build()));
        doThrow(new DataIntegrityViolationException("constraint"))
                .when(contactService).update(uuid, updateRequest);

        VolunteerServiceException ex = assertThrows(
                VolunteerServiceException.class,
                () -> volunteerService.update(Base.USERNAME_FOR_UPDATE, updateRequest)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertThat(ex.getMessage()).contains("Email or phone");
        verify(volunteerRepository, never()).findById(any());
    }

    @Disabled
    @Test
    public void proveAction_success() {
        //todo add incidentService for check current action
        ProveVolunteer prove = ProveVolunteer.builder().incNumber(1).action(ACTION.PROVE).build();
        volunteerService.proveAction(prove, "Prover01");
    }

    @Test
    public void getByRequest_success() {
        VolunteerListRequest request = VolunteerListRequest.builder().city("Moscow").status(STATUS.AVAILABLE).build();

        when(volunteerRepository.get(request))
                .thenReturn(List.of(Volunteer.builder().firstName("FIRST").build()));

        VolunteerInfosResponse response = volunteerService.get(request);

        assertEquals(1, response.getVolunteers().size());
        assertEquals("FIRST", response.getVolunteers().get(0).getFirstName());
    }

    @Test
    public void getByRequest_emptyResult() {
        VolunteerListRequest request = VolunteerListRequest.builder().build();
        when(volunteerRepository.get(request)).thenReturn(List.of());

        VolunteerInfosResponse response = volunteerService.get(request);

        assertThat(response.getVolunteers()).isEmpty();
    }

    @Test
    public void getByUUID_success() {
        UUID requestedUUID = UUID.randomUUID();
        Optional<Volunteer> volunteer = Optional.of(Volunteer.builder().firstName("Руслан").id(requestedUUID).build());
        when(volunteerRepository.findById(requestedUUID)).thenReturn(volunteer);

        VolunteerInfoDto volunteerInfoDto = volunteerService.get(requestedUUID);

        assertEquals(volunteer.get().getFirstName(), volunteerInfoDto.getFirstName());
    }

    @Test
    public void getByUUID_notFound() {
        UUID requestedUUID = UUID.randomUUID();
        when(volunteerRepository.findById(requestedUUID)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(
                VolunteerServiceException.class,
                () -> volunteerService.get(requestedUUID)
        ).getStatus());
    }
}

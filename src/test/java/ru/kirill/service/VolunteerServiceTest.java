package ru.kirill.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;
import ru.kirill.Base;
import ru.kirill.controller.dto.*;
import ru.kirill.controller.exception.VolunteerServiceException;
import ru.kirill.mapper.VolunteerMapperImpl;
import ru.kirill.storage.ContractRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VolunteerServiceTest {

    @Mock
    private VolunteerRep volunteerRepository;
    @Mock
    private ContractRepository contractRepository;

    private VolunteerService volunteerService;

    @BeforeEach
    public void setUp() {
        VolunteerMapperImpl mapper = new VolunteerMapperImpl();
        ReflectionTestUtils.setField(mapper, "fioParser", new FioParser());
        volunteerService = new VolunteerServiceImpl(volunteerRepository, mapper, contractRepository);
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
        when(volunteerRepository.save(any(Volunteer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        VolunteerInfoDto volunteerInfoDto = volunteerService.create(request, Base.STEPANOV01);
        assertThat(volunteerInfoDto.getId()).isGreaterThanOrEqualTo(0);
        assertThat(volunteerInfoDto.getStatus()).isEqualTo(STATUS.AVAILABLE);
        assertThat(volunteerInfoDto.getFirstName()).isEqualTo("Владимир");
        assertThat(volunteerInfoDto.getLastName()).isEqualTo("Степанов");
        assertThat(volunteerInfoDto.getMiddleName()).isEqualTo("Сергеевич");
        assertThat(volunteerInfoDto.getUserId()).isEqualTo(Base.STEPANOV01);
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
        when(volunteerRepository.save(any(Volunteer.class))).thenThrow(new DataIntegrityViolationException("constraint"));
        assertThrows(
                VolunteerServiceException.class,
                () -> volunteerService.create(request, Base.STEPANOV01)
        );
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
        Volunteer volunteerInfo = Volunteer.builder().firstName(Base.USER_FOR_DELETE_TEST).build();
        when(volunteerRepository.findByUserId(Base.USER_FOR_DELETE_TEST)).thenReturn(List.of(volunteerInfo));
        List<Volunteer> volunteer = volunteerRepository.findByUserId(Base.USER_FOR_DELETE_TEST);
        assertThat(volunteer).isNotEmpty();

        volunteerService.delete(Base.USER_FOR_DELETE_TEST);

        when(volunteerRepository.findByUserId(Base.USER_FOR_DELETE_TEST)).thenReturn(List.of());
        List<Volunteer> notExistsVolunteer = volunteerRepository.findByUserId(Base.USER_FOR_DELETE_TEST);
        assertThat(notExistsVolunteer).isEmpty();

        verify(volunteerRepository).deleteByUserId(Base.USER_FOR_DELETE_TEST);
    }

    @Test
    public void updateTest_success() {
        UUID uuid = UUID.randomUUID();
        when(volunteerRepository.findByUserId(Base.USERNAME_FOR_UPDATE))
                .thenReturn(List.of(Volunteer.builder().id(uuid).firstName("Пётр").lastName("Васюков").createDate(LocalDateTime.of(2026, 9, 6, 12, 12)).build()));
        when(volunteerRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(Volunteer.builder().firstName("Пётр").lastName("Васюткин").createDate(LocalDateTime.of(2026, 9, 6, 12, 12)).build()));
        when(volunteerRepository.update(any(), any(UpdateVolunteerRequest.class))).thenReturn(1);
        List<Volunteer> volunteer = volunteerRepository.findByUserId(Base.USERNAME_FOR_UPDATE);
        assertEquals("Васюков", volunteer.getFirst().getLastName());

        UpdateVolunteerRequest updateRequest = UpdateVolunteerRequest.builder().lastName("Васюткин").build();
        VolunteerInfoDto updated = volunteerService.update(Base.USERNAME_FOR_UPDATE, updateRequest);

        assertEquals("Васюткин", updated.getLastName());

        verify(volunteerRepository).update(any(), any(UpdateVolunteerRequest.class));
    }

    @Test
    public void updateTest_notFoundVolunteer() {
        when(volunteerRepository.update(any(), any(UpdateVolunteerRequest.class))).thenReturn(0);

        UpdateVolunteerRequest updateRequest = UpdateVolunteerRequest.builder().lastName("Васюткин").build();

        assertTrue(
                assertThrows(
                        VolunteerServiceException.class,
                        () -> volunteerService.update(Base.USERNAME_FOR_UPDATE, updateRequest)
                ).getStatus().is4xxClientError()
        );
    }

    @Test
    public void proveAction_success() {
        //todo add incidentService for check current action
        ProveVolunteer prove = ProveVolunteer.builder().incNumber(1).action(ACTION.PROVE).build();
        volunteerService.proveAction(prove, "Prover01");
    }

    @Test
    public void getById_success() {
        Volunteer exampleVolunteer = Volunteer.builder().firstName("Кирилл").userId("Kirill01").build();
        when(volunteerRepository.findByUserId(Base.USERNAME)).thenReturn(List.of(exampleVolunteer));
        List<Volunteer> volunteer = volunteerRepository.findByUserId(Base.USERNAME);

        when(volunteerRepository.findByUserId("Kirill01")).thenReturn(List.of(exampleVolunteer));
        VolunteerInfoDto volunteer2 = volunteerService.get(volunteer.getFirst().getUserId());
        assertEquals("Кирилл", volunteer2.getFirstName());
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
    public void getByRequest_success_noFiltersProvided() {
        VolunteerListRequest request = VolunteerListRequest.builder().build();

        when(volunteerRepository.get(request))
                .thenReturn(List.of(Volunteer.builder().firstName("FIRST").build()));

        VolunteerInfosResponse response = volunteerService.get(request);

        assertEquals(1, response.getVolunteers().size());
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
    public void getByUUID_notFount() {
        UUID requestedUUID = UUID.randomUUID();
        when(volunteerRepository.findById(requestedUUID)).thenReturn(Optional.empty());
        assertTrue(
                assertThrows(
                        VolunteerServiceException.class,
                        () -> volunteerService.get(requestedUUID)
                ).getStatus().is4xxClientError()
        );
    }
}

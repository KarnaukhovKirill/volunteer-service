package ru.kirill.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.controller.dto.*;
import ru.kirill.controller.exception.VolunteerServiceException;
import ru.kirill.mapper.VolunteerMapper;
import ru.kirill.storage.location.LocationRep;
import ru.kirill.storage.entity.Location;
import ru.kirill.storage.entity.Volunteer;
import ru.kirill.storage.volunteer.VolunteerRep;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VolunteerServiceImpl implements VolunteerService {
    private final VolunteerRep volunteerRepository;
    private final VolunteerMapper mapper;
    private final ContactService contactService;
    private final LocationRep locationRep;

    public VolunteerServiceImpl(VolunteerRep volunteerRepository, VolunteerMapper mapper, ContactService contactService, LocationRep locationRep) {
        this.volunteerRepository = volunteerRepository;
        this.mapper = mapper;
        this.contactService = contactService;
        this.locationRep = locationRep;
    }

    /**
     * Сохраняет волонтёра вместе с локацией и контактами в одной транзакции.
     * saveAndFlush и flush нужны, чтобы нарушение unique всплыло здесь, а не при коммите.
     */
    @Transactional
    @Override
    public VolunteerInfoDto create(CreateVolunteerRequest request, String userId) {
        Volunteer volunteer = mapper.toDomain(request).toBuilder()
                .userId(userId)
                .location(locationRep.findOrCreate(Location.buildName(request.getCity(), request.getRestrict())))
                .build();
        Volunteer savedVolunteer;
        try {
            savedVolunteer = volunteerRepository.saveAndFlush(volunteer);
        } catch (DataIntegrityViolationException e) {
            String errorText = String.format("Item with this userId %s already exists", userId);
            log.error(errorText);
            throw new VolunteerServiceException(errorText, HttpStatus.CONFLICT);
        }
        try {
            contactService.create(savedVolunteer, request.getEmail(), request.getPhoneNumber());
            volunteerRepository.flush();
        } catch (DataIntegrityViolationException e) {
            String errorText = "Email or phone number is already used by another volunteer";
            log.error(errorText);
            throw new VolunteerServiceException(errorText, HttpStatus.CONFLICT);
        }
        return mapper.toDto(savedVolunteer);
    }

    @Override
    public VolunteerInfoDto get(String username) {
        return volunteerRepository.findByUserId(username)
                .stream()
                .map(mapper::toDto)
                .findFirst()
                .orElseThrow(() -> {
                            String msg = String.format("Volunteer with name = %s not found", username);
                            return new VolunteerServiceException(msg, HttpStatus.NOT_FOUND);
                        }
                );
    }

    @Override
    public void delete(String username) {
        volunteerRepository.deleteByUserId(username);
    }

    /**
     * Обновляет волонтёра и контакты в одной транзакции, чтобы при конфликте контактов откатился и UPDATE волонтёра.
     * flush нужен, чтобы нарушение unique всплыло внутри try, а не при коммите.
     */
    @Transactional
    @Override
    public VolunteerInfoDto update(String userId, UpdateVolunteerRequest request) {
        int id = volunteerRepository.update(userId, request);
        if (id == 0) {
            var msg = String.format("Volunteer with userId = %s not found", userId);
            throw new VolunteerServiceException(msg, HttpStatus.NOT_FOUND);
        }
        UUID volunteereUUID = volunteerRepository.findByUserId(userId).stream()
                .findFirst()
                .map(Volunteer::getId)
                .orElseThrow(() -> {
                    var msg = String.format("Volunteer with userId = %s not found", userId);
                    return new VolunteerServiceException(msg, HttpStatus.NOT_FOUND);
                });
        try {
            contactService.update(volunteereUUID, request);
            volunteerRepository.flush();
        } catch (DataIntegrityViolationException e) {
            String errorText = "Email or phone number is already used by another volunteer";
            log.error(errorText);
            throw new VolunteerServiceException(errorText, HttpStatus.CONFLICT);
        }
        return get(volunteereUUID);
    }

    @Override
    public void proveAction(ProveVolunteer prove, String userId) {
        //todo add another service
    }

    @Override
    public VolunteerInfosResponse get(VolunteerListRequest request) {
        List<Volunteer> volunteers = volunteerRepository.get(request);
        return new VolunteerInfosResponse(mapper.toDto(volunteers));
    }

    @Override
    public VolunteerInfoDto get(UUID id) {
        return volunteerRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> {
                            String msg = String.format("Volunteer with id = %s not found", id);
                            return new VolunteerServiceException(msg, HttpStatus.NOT_FOUND);
                        }
                );
    }
}

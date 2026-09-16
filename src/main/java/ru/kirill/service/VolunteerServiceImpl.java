package ru.kirill.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.kirill.controller.dto.*;
import ru.kirill.controller.exception.VolunteerServiceException;
import ru.kirill.storage.ContractRepository;
import ru.kirill.storage.Volunteer;
import ru.kirill.storage.VolunteerRep;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VolunteerServiceImpl implements VolunteerService {
    private final VolunteerRep volunteerRepository;
    private final VolunteerMapper mapper;
    private final ContractRepository contractRepository;

    public VolunteerServiceImpl(VolunteerRep volunteerRepository, VolunteerMapper mapper, ContractRepository contractRepository) {
        this.volunteerRepository = volunteerRepository;
        this.mapper = mapper;
        this.contractRepository = contractRepository;
    }

    @Override
    public VolunteerInfoDto create(CreateVolunteerRequest request, String userId) {
        Volunteer volunteer = mapper.toDomain(request).toBuilder()
                .userId(userId)
                .build();
        try {
        Volunteer savedVolunteer = volunteerRepository.save(volunteer);
        return mapper.toDto(savedVolunteer);
        } catch (DataIntegrityViolationException e) {
            String errorText = String.format("Item with this userId %s already exists", userId);
            log.error(errorText);
            throw new VolunteerServiceException(errorText, HttpStatus.CONFLICT);
        }
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

    @Override
    public VolunteerInfoDto update(String userId, UpdateVolunteerRequest request) {
        UUID id = volunteerRepository.update(userId, request);
        contractRepository.update(id, request);
        return get(id);
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

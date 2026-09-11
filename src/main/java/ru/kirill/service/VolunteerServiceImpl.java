package ru.kirill.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.kirill.controller.dto.*;
import ru.kirill.controller.exception.VolunteerServiceException;
import ru.kirill.storage.VolunteerInfo;
import ru.kirill.storage.VolunteerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class VolunteerServiceImpl implements VolunteerService {
    private final VolunteerRepository volunteerRepository;
    private final VolunteerMapper mapper;

    public VolunteerServiceImpl(VolunteerRepository volunteerRepository, VolunteerMapper mapper) {
        this.volunteerRepository = volunteerRepository;
        this.mapper = mapper;
    }

    @Override
    public VolunteerInfoDto create(CreateVolunteerRequest request, String userId) {
        VolunteerInfo volunteerInfo = mapper.toDomain(request).toBuilder()
                .userId(userId)
                .build();
//        try {
        VolunteerInfo volunteerI = volunteerRepository.create(volunteerInfo);
        return mapper.toDto(volunteerI);
//        } catch (DataIntegrityViolationException e) {
//            String errorText = String.format("Item with this userId %s already exists", userId);
//            log.error(errorText);
//            throw new VolunteerServiceException(errorText, HttpStatus.CONFLICT)
//        }
    }

    @Override
    public VolunteerInfoDto get(String username) {
        return volunteerRepository.findByName(username)
                .map(mapper::toDto)
                .orElseThrow(() -> {
                            String msg = String.format("Volunteer with name = %s not found", username);
                            return new VolunteerServiceException(msg, HttpStatus.NOT_FOUND);
                        }
                );
    }

    @Override
    public void delete(String username) {
        volunteerRepository.delete(username);
    }

    @Override
    public VolunteerInfoDto update(String userId, UpdateVolunteerRequest request) {
        UUID id = volunteerRepository.update(userId, request);
        return get(id);
    }

    @Override
    public void proveAction(ProveVolunteer prove, String userId) {

    }

    @Override
    public VolunteerInfosResponse get(VolunteerListRequest request) {
        List<VolunteerInfo> volunteers = volunteerRepository.get(request);
        return new VolunteerInfosResponse(mapper.toDto(volunteers));
    }

    @Override
    public VolunteerInfoDto get(UUID id) {
        return volunteerRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> {
                            String msg = String.format("Volunteer with id = %d not found", id);
                            return new VolunteerServiceException(msg, HttpStatus.NOT_FOUND);
                        }
                );
    }
}

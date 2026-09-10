package ru.kirill.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.kirill.controller.dto.*;
import ru.kirill.controller.exception.VolunteerServiceException;
import ru.kirill.storage.VolunteerInfo;
import ru.kirill.storage.VolunteerRepository;

import java.util.Optional;

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
        VolunteerInfo volunteerInfo = mapper.toDomain(request);
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
        Optional<VolunteerInfo> volunteerOptional = volunteerRepository.findByName(username);
        if (volunteerOptional.isPresent()) {
            return VolunteerInfo.toDto(volunteerOptional.get());
        }
        return VolunteerInfoDto.builder().build();
    }

    @Override
    public void delete(String username) {

    }

    @Override
    public VolunteerInfoDto update(String userId, UpdateVolunteerRequest request) {
        return null;
    }

    @Override
    public void proveAction(ProveVolunteer prove, String userId) {

    }

    @Override
    public VolunteerInfosResponse get(VolunteerListRequest request) {
        return null;
    }

    @Override
    public VolunteerInfoDto get(long id) {
        return null;
    }
}

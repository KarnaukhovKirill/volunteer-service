package ru.kirill.controller.external;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kirill.controller.dto.*;
import ru.kirill.service.VolunteerService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/volunteer")
public class VolunteerController {
    public static final String USER_HEADER = "X-USER-ID";

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @PostMapping("/register/me")
    @ResponseStatus(code = HttpStatus.CREATED)
    public VolunteerInfoDto create(@RequestBody @Valid CreateVolunteerRequest request, @RequestHeader(USER_HEADER) String userId) {
        log.info("input request: {} ", request);
        return volunteerService.create(request, userId);
    }

    @GetMapping("/me")
    public VolunteerInfoDto get(@RequestHeader(USER_HEADER) String userId) {
        log.info("input userId {}", userId);
        return volunteerService.get(userId);
    }

    @DeleteMapping("/me")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(USER_HEADER) String userId) {
        log.info("input userId {}", userId);
        volunteerService.delete(userId);
    }

    @PatchMapping("/me")
    public VolunteerInfoDto patch(@RequestHeader(USER_HEADER) String userId, @RequestBody @Valid UpdateVolunteerRequest request) {
        log.info("input userId {}", userId);
        return volunteerService.update(userId, request);
    }

    @PostMapping("/me/incident/act")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void proveAction(@RequestBody @Valid ProveVolunteer proveVolunteer, @RequestHeader(USER_HEADER) String userId) {
        log.info("input proveVolunteer {}", proveVolunteer);
        volunteerService.proveAction(proveVolunteer, userId);
    }

    @GetMapping("/{id}")
    public VolunteerInfoDto get(@PathVariable("id") @Positive(message = "id должен быть > 0.") UUID id) {
        log.info("input id {}", id);
        return volunteerService.get(id);
    }

    @PostMapping("/list")
    public VolunteerInfosResponse list(@RequestBody @Valid VolunteerListRequest request) {
        log.info("input request {}", request);
        return volunteerService.get(request);
    }
}

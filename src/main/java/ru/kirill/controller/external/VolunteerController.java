package ru.kirill.controller.external;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kirill.controller.dto.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/volunteer")
public class VolunteerController {

    public static final String USER_HEADER = "X-USER_ID";

    @PostMapping("/register/me")
    @ResponseStatus(code = HttpStatus.CREATED)
    public VolunteerInfo create(@RequestBody @Valid CreateVolunteerRequest request) {
        log.info("input request: {} ", request);
        //service.create(request);
        return new VolunteerInfo(1);
    }

    @GetMapping("/me")
    public VolunteerInfo get(@RequestHeader(USER_HEADER) String username) {
        log.info("input username {}", username);
        //service.get(username);
        return new VolunteerInfo(1);
    }

    @DeleteMapping("/me")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(USER_HEADER) String username) {
        log.info("input username {}", username);
        //todo add service.delete(username);
    }

    @PatchMapping("/me")
    public VolunteerInfo patch(@RequestHeader(USER_HEADER) String username, @RequestBody @Valid UpdateVolunteerRequest request) {
        log.info("input username {}", username);
        //service.update(username, request);
        return new VolunteerInfo(1);
    }

    @PostMapping("/me/incident/act")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void proveAction(@RequestBody @Valid ProveVolunteer proveVolunteer) {
        //service.proveAction(proveVolunteer);
        log.info("input proveVolunteer {}", proveVolunteer);
    }

    @GetMapping("/{id}")
    public VolunteerInfo get(@PathVariable("id") @Positive(message = "id должен быть > 0.") Long id) {
        log.info("input id {}", id);
        //service.get(id);
        return new VolunteerInfo(1);
    }

    @PostMapping("/list")
    public VolunteerInfosResponse list(@RequestBody @Valid VolunteerListRequest request) {
        log.info("input request {}", request);
        //service.get(request);
        return new VolunteerInfosResponse(List.of(new VolunteerInfo(1)));
    }
}

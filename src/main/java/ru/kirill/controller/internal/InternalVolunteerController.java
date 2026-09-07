package ru.kirill.controller.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.kirill.controller.dto.ContactsVolunteerResponse;
import ru.kirill.controller.dto.VolunteerContact;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("internal/api/v1/volunteer")
public class InternalVolunteerController {

    @PostMapping("/list")
    public ContactsVolunteerResponse list(@RequestBody List<Long> ids) {
        return new ContactsVolunteerResponse(List.of(VolunteerContact.builder().email("email").build()));
    }
}

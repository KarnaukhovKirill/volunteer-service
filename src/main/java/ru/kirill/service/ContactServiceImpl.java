package ru.kirill.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.controller.dto.CONTACTTYPE;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.storage.contact.ContactRep;
import ru.kirill.storage.entity.ContactInfo;
import ru.kirill.storage.entity.Volunteer;
import ru.kirill.storage.volunteer.VolunteerRep;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService {
    private final ContactRep contactRep;
    private final VolunteerRep volunteerRepository;

    public ContactServiceImpl(ContactRep contactRep, VolunteerRep volunteerRepository) {
        this.contactRep = contactRep;
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public void create(Volunteer volunteer, String email, String phoneNumber) {
        contactRep.save(newContact(volunteer, CONTACTTYPE.EMAIL, email));
        contactRep.save(newContact(volunteer, CONTACTTYPE.PHONE, phoneNumber));
    }

    @Transactional
    @Override
    public void update(UUID userId, UpdateVolunteerRequest request) {
        Volunteer volunteer = volunteerRepository.getReferenceById(userId);
        List<ContactInfo> contacts = contactRep.getByVolunteerId(volunteer);
        saveContact(volunteer, contacts, CONTACTTYPE.EMAIL, request.getEmail());
        saveContact(volunteer, contacts, CONTACTTYPE.PHONE, request.getPhoneNumber());
    }

    private void saveContact(Volunteer volunteer, List<ContactInfo> contacts, CONTACTTYPE type, String contact) {
        if (contact == null) {
            return;
        }
        contacts.stream()
                .filter(info -> info.getContactType() == type)
                .findFirst()
                .ifPresentOrElse(
                        info -> info.setContact(contact),
                        () -> contactRep.save(newContact(volunteer, type, contact))
                );
    }

    private static ContactInfo newContact(Volunteer volunteer, CONTACTTYPE type, String contact) {
        return ContactInfo.builder()
                .contact(contact)
                .contactType(type)
                .createDate(LocalDateTime.now())
                .volunteerId(volunteer)
                .build();
    }
}

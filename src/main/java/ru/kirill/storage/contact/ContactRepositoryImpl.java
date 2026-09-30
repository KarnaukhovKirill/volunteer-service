package ru.kirill.storage.contact;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.controller.dto.CONTACTTYPE;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.storage.entity.ContactInfo;
import ru.kirill.storage.entity.Volunteer;
import ru.kirill.storage.volunteer.VolunteerRep;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ContactRepositoryImpl implements ContactRepository {
    private final ContactRep contactRep;
    private final VolunteerRep volunteerRepository;

    public ContactRepositoryImpl(ContactRep contactRep, VolunteerRep volunteerRepository) {
        this.contactRep = contactRep;
        this.volunteerRepository = volunteerRepository;
    }

    /**
     * Обновляет email и телефон независимо друг от друга: не переданный в запросе контакт не меняется.
     * Транзакция нужна, чтобы изменения найденных контактов сохранились в БД.
     */
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
                        () -> contactRep.save(
                                ContactInfo.builder()
                                        .contact(contact)
                                        .contactType(type)
                                        .createDate(LocalDateTime.now())
                                        .volunteerId(volunteer)
                                        .build()
                        )
                );
    }
}

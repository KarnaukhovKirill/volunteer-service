package ru.kirill.storage.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.Base;
import ru.kirill.BaseTests;
import ru.kirill.controller.dto.CONTACTTYPE;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.storage.contact.ContactRepository;
import ru.kirill.storage.contact.ContactRepositoryImpl;
import ru.kirill.storage.entity.ContactInfo;
import ru.kirill.storage.searchers.VolunteerSearcherers;
import ru.kirill.storage.updaters.VolunteerUpdaters;
import ru.kirill.storage.volunteer.VolunteerRepositoryImpl;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({ContactRepositoryImpl.class, VolunteerRepositoryImpl.class, VolunteerUpdaters.class, VolunteerSearcherers.class})
@Transactional(propagation = Propagation.NEVER)
public class ContactRepositoryTest extends BaseTests {
    private static final UUID USERNAME_ID = UUID.fromString(Base.USERNAME_ID);

    @Autowired
    private EntityManager em;
    @Autowired
    private ContactRepository contactRepository;

    @Test
    public void updateExistingEmail() {
        contactRepository.update(USERNAME_ID, UpdateVolunteerRequest.builder().email("new@mail.com").build());

        assertEquals(Map.of(CONTACTTYPE.EMAIL, "new@mail.com", CONTACTTYPE.PHONE, "79990000000"), getContacts(USERNAME_ID));
    }

    @Test
    public void updateExistingPhone() {
        contactRepository.update(USERNAME_ID, UpdateVolunteerRequest.builder().phoneNumber("79991111111").build());

        assertEquals(Map.of(CONTACTTYPE.EMAIL, "username@mail.com", CONTACTTYPE.PHONE, "79991111111"), getContacts(USERNAME_ID));
    }

    @Test
    public void updateEmailAndPhone() {
        contactRepository.update(USERNAME_ID, UpdateVolunteerRequest.builder()
                .email("new@mail.com")
                .phoneNumber("79991111111")
                .build());

        assertEquals(Map.of(CONTACTTYPE.EMAIL, "new@mail.com", CONTACTTYPE.PHONE, "79991111111"), getContacts(USERNAME_ID));
    }

    @Test
    public void createContactsWhenAbsent() {
        UUID volunteerId = getVolunteerId("User001");

        contactRepository.update(volunteerId, UpdateVolunteerRequest.builder()
                .email("user001@mail.com")
                .phoneNumber("79992222222")
                .build());

        assertEquals(Map.of(CONTACTTYPE.EMAIL, "user001@mail.com", CONTACTTYPE.PHONE, "79992222222"), getContacts(volunteerId));
    }

    @Test
    public void createOnlyPassedContact() {
        UUID volunteerId = getVolunteerId("User001");

        contactRepository.update(volunteerId, UpdateVolunteerRequest.builder().email("user001@mail.com").build());

        assertEquals(Map.of(CONTACTTYPE.EMAIL, "user001@mail.com"), getContacts(volunteerId));
    }

    @Test
    public void noContactsInRequestKeepsExisting() {
        contactRepository.update(USERNAME_ID, UpdateVolunteerRequest.builder().lastName("Ivanov").build());

        assertEquals(Map.of(CONTACTTYPE.EMAIL, "username@mail.com", CONTACTTYPE.PHONE, "79990000000"), getContacts(USERNAME_ID));
    }

    @Test
    public void noContactsInRequestCreatesNothing() {
        UUID volunteerId = getVolunteerId("User001");

        contactRepository.update(volunteerId, UpdateVolunteerRequest.builder().lastName("Ivanov").build());

        assertEquals(Map.of(), getContacts(volunteerId));
    }

    private UUID getVolunteerId(String userId) {
        return volunteerRepository.findByUserId(userId).getFirst().getId();
    }

    private Map<CONTACTTYPE, String> getContacts(UUID volunteerId) {
        return em.createQuery("select c from ContactInfo c where c.volunteerId.id = ?1", ContactInfo.class)
                .setParameter(1, volunteerId)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(ContactInfo::getContactType, ContactInfo::getContact));
    }
}

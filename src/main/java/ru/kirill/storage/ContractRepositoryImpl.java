package ru.kirill.storage;

import org.springframework.stereotype.Repository;
import ru.kirill.controller.dto.CONTACTTYPE;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.storage.entity.ContactInfo;
import ru.kirill.storage.volunteer.VolunteerRep;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ContractRepositoryImpl implements ContractRepository {
    private final ContactRep contactRep;
    private final VolunteerRep volunteerRepository;

    public ContractRepositoryImpl(ContactRep contactRep, VolunteerRep volunteerRepository) {
        this.contactRep = contactRep;
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public void update(UUID userId, UpdateVolunteerRequest request) {
        Optional<ContactInfo> first = contactRep.getByVolunteerId(volunteerRepository.getReferenceById(userId))
                .stream()
                .findFirst();
        if (first.isPresent()) {
            if (request.getEmail() != null) {
                first.get().setContact(request.getEmail());
                first.get().setContactType(CONTACTTYPE.EMAIL);
            } else {
                first.get().setContact(request.getPhoneNumber());
                first.get().setContactType(CONTACTTYPE.PHONE);
            }
        } else {
            contactRep.save(
                    ContactInfo.builder()
                            .contact(request.getEmail() != null ? request.getEmail() : request.getPhoneNumber())
                            .contactType(request.getEmail() != null ? CONTACTTYPE.EMAIL : CONTACTTYPE.PHONE)
                            .createDate(LocalDateTime.now())
                            .volunteerId(volunteerRepository.getReferenceById(userId))
                            .build()
            );
        }
    }
}

package ru.kirill.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.kirill.Base;
import ru.kirill.BaseIntegrationTest;
import ru.kirill.controller.dto.*;
import ru.kirill.controller.external.VolunteerController;
import ru.kirill.storage.entity.ContactInfo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class VolunteerControllerTest extends BaseIntegrationTest {

    public static final String BASE_URL = "/api/v1/volunteer/";
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testCreateVolunteer_response_is201() {
        webTestClient.post()
                .uri(BASE_URL + "register/me")
                .header(VolunteerController.USER_HEADER, Base.NEW_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(CreateVolunteerRequest.builder()
                        .fio("Фамилия Имя Отчество")
                        .phoneNumber("79066546654")
                        .gender(GENDER.MALE)
                        .email("ya@mail.com")
                        .birthday(LocalDate.of(1993, 1, 2))
                        .city("Moscow")
                        .restrict("Мытищи")
                        .build()
                )
                .exchange()
                .expectStatus().isCreated()
                .expectBody(VolunteerInfoDto.class)
                .value(response -> {
                    assertThat(response.getUserId()).isEqualTo(Base.NEW_USERNAME);
                    assertThat(response.getLastName()).isEqualTo("Фамилия");
                    assertThat(response.getFirstName()).isEqualTo("Имя");
                    assertThat(response.getMiddleName()).isEqualTo("Отчество");
                    assertThat(response.getStatus()).isEqualTo(STATUS.AVAILABLE);
                    assertThat(response.getCreateDate()).isNotNull();
                });

        UUID volunteerId = volunteerRepository.findByUserId(Base.NEW_USERNAME).getFirst().getId();
        assertThat(getContacts(volunteerId)).isEqualTo(Map.of(CONTACTTYPE.EMAIL, "ya@mail.com", CONTACTTYPE.PHONE, "79066546654"));
        assertThat(getLocationName(Base.NEW_USERNAME)).isEqualTo("Moscow(Мытищи)");
    }

    @Test
    public void testCreateVolunteer_response_is4xx() {
        webTestClient.post()
                .uri(BASE_URL + "register/me")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(CreateVolunteerRequest.builder().fio(null).build())
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void testCreateVolunteer_response_isConflict() {
        webTestClient.post()
                .uri(BASE_URL + "register/me")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(CreateVolunteerRequest.builder()
                        .fio("Фамилия Имя Отчество")
                        .phoneNumber("79066546654")
                        .gender(GENDER.MALE)
                        .email("ya@mail.com")
                        .birthday(LocalDate.of(1993, 1, 2))
                        .city("Moscow")
                        .restrict("Мытищи")
                        .build())
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void testGetVolunteer_response_success() {
        webTestClient.get()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VolunteerInfoDto.class)
                .value(VolunteerControllerTest::assertIsUsername);
    }

    @Test
    public void testDeleteVolunteer_response_success() {
        webTestClient.delete()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .exchange()
                .expectStatus().isNotFound();
        assertThat(getContacts(UUID.fromString(Base.USERNAME_ID))).isEmpty();
    }

    @Test
    public void testDeleteNotExistingVolunteer_response_success() {
        webTestClient.delete()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, Base.NOT_EXIST_USERNAME)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void testPatchVolunteer_response_success() {
        webTestClient.patch()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateVolunteerRequest.builder().city("Piter").restrict("Vaska").lastName("Petrov").email("petrov@gmail.com").phoneNumber("+7778882354").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VolunteerInfoDto.class)
                .value(response -> {
                    assertThat(response.getUserId()).isEqualTo(Base.USERNAME);
                    assertThat(response.getLastName()).isEqualTo("Petrov");
                    assertThat(response.getFirstName()).isEqualTo("Имя");
                    assertThat(response.getUpdateDate()).isNotNull();
                });

        assertThat(getContacts(UUID.fromString(Base.USERNAME_ID)))
                .isEqualTo(Map.of(CONTACTTYPE.EMAIL, "petrov@gmail.com", CONTACTTYPE.PHONE, "+7778882354"));
        assertThat(getLocationName(Base.USERNAME)).isEqualTo("Piter(Vaska)");
    }

    @Test
    public void testPatchVolunteer_onlyLastName_response_success() {
        webTestClient.patch()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateVolunteerRequest.builder().lastName("Petrov").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VolunteerInfoDto.class)
                .value(response -> assertThat(response.getLastName()).isEqualTo("Petrov"));
    }

    @Test
    public void testPatchVolunteer_cityWithoutRestrict_response_success() {
        webTestClient.patch()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateVolunteerRequest.builder().city("Kazan").build())
                .exchange()
                .expectStatus().isOk();

        assertThat(getLocationName(Base.USERNAME)).isEqualTo("Kazan");
    }

    @Test
    public void testPostListVolunteer_response_success() {
        webTestClient.post()
                .uri(BASE_URL + "list")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(VolunteerListRequest.builder().city("Moscow").status(STATUS.AVAILABLE).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(VolunteerInfosResponse.class)
                .value(response -> {
                    int size = response.getVolunteers().size();
                    assertThat(size).isEqualTo(1);
                });
    }

    @Test
    public void testGetVolunteerById_response_success() {
        webTestClient.get()
                .uri(BASE_URL + Base.USERNAME_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VolunteerInfoDto.class)
                .value(VolunteerControllerTest::assertIsUsername);
    }

    @Test
    public void testPostVolunteersByIds_response_success() {
        webTestClient.post()
                .uri("/internal/api/v1/volunteer/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(1, 2, 3))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ContactsVolunteerResponse.class)
                .value(response ->
                        assertThat(response.getContactInfoDtos().size()).isEqualTo(1));
    }

    @Test
    public void testPostProveVolunteerInIncident_response_success() {
        webTestClient.post()
                .uri(BASE_URL + "/me/incident/act")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProveVolunteer.builder().incNumber(1).action(ACTION.PROVE).build())
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void testPostProveVolunteerInIncident_response_is4xx() {
        webTestClient.post()
                .uri(BASE_URL + "/me/incident/act")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProveVolunteer.builder().incNumber(1).build())
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void testGetMe_response_is404() {
        webTestClient.get()
                .uri(BASE_URL + "/me")
                .header(VolunteerController.USER_HEADER, Base.NOT_EXIST_USERNAME)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    assertThat(problemDetail.getDetail()).isNotEmpty();
                    assertThat(problemDetail.getDetail()).isEqualTo("Volunteer with name = RandomUsername not found");
                });

    }

    @Test
    public void testGetByUUID_response_is404() {
        UUID randomUUID = UUID.randomUUID();
        webTestClient.get()
                .uri(BASE_URL + randomUUID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    assertThat(problemDetail.getDetail()).isEqualTo("Volunteer with id = " + randomUUID + " not found");
                });
    }

    @Test
    public void testPatchVolunteer_response_is409() {

        webTestClient.patch()
                .uri(BASE_URL + "/me")
                .header(VolunteerController.USER_HEADER, Base.NOT_EXIST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateVolunteerRequest.builder().phoneNumber("+79115841122").build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    assertThat(problemDetail.getDetail()).isEqualTo("Volunteer with userId = RandomUsername not found");
                });
    }

    @Test
    public void testPatchVolunteer_emailUsedByAnotherVolunteer_response_is409_andRollback() {
        webTestClient.post()
                .uri(BASE_URL + "register/me")
                .header(VolunteerController.USER_HEADER, Base.NEW_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(withField(validCreateRequest(), "email", "taken@mail.com"))
                .exchange()
                .expectStatus().isCreated();

        webTestClient.patch()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateVolunteerRequest.builder().lastName("Petrov").email("taken@mail.com").build())
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ProblemDetail.class)
                .value(problemDetail ->
                        assertThat(problemDetail.getDetail()).isEqualTo("Email or phone number is already used by another volunteer"));

        assertThat(volunteerRepository.findByUserId(Base.USERNAME).getFirst().getLastName()).isEqualTo("Фамилия");
        assertThat(getContacts(UUID.fromString(Base.USERNAME_ID)))
                .isEqualTo(Map.of(CONTACTTYPE.EMAIL, "username@mail.com", CONTACTTYPE.PHONE, "79990000000"));
    }

    @Test
    public void testGetVolunteerWithoutXUserId_response_is400() {
        webTestClient.get()
                .uri(BASE_URL + "/me")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetVolunteerWithInvalidUUID_response_is400() {
        webTestClient.get()
                .uri(BASE_URL + "invalidId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testPathWithInvalidJson_response_is400() {
        webTestClient.patch()
                .uri(BASE_URL + "/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .bodyValue("""
                                                "invalidjson"
                        """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest(name = "{0} = [{1}]")
    @MethodSource("invalidCreateFields")
    public void testCreateVolunteer_invalidField_response_is400(String field, Object value) {
        webTestClient.post()
                .uri(BASE_URL + "register/me")
                .header(VolunteerController.USER_HEADER, Base.NEW_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(withField(validCreateRequest(), field, value))
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(volunteerRepository.findByUserId(Base.NEW_USERNAME)).isEmpty();
    }

    @Test
    public void testCreateVolunteer_withoutRestrict_response_is201() {
        webTestClient.post()
                .uri(BASE_URL + "register/me")
                .header(VolunteerController.USER_HEADER, Base.NEW_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(withField(validCreateRequest(), "restrict", null))
                .exchange()
                .expectStatus().isCreated();

        assertThat(getLocationName(Base.NEW_USERNAME)).isEqualTo("Moscow");
    }

    @ParameterizedTest(name = "{0} = [{1}]")
    @MethodSource("invalidPatchFields")
    public void testPatchVolunteer_blankField_response_is400(String field, String value) {
        webTestClient.patch()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(field, value))
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(volunteerRepository.findByUserId(Base.USERNAME).getFirst().getLastName()).isEqualTo("Фамилия");
    }

    @Test
    public void testPatchVolunteer_emptyBody_response_success() {
        webTestClient.patch()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of())
                .exchange()
                .expectStatus().isOk()
                .expectBody(VolunteerInfoDto.class)
                .value(response -> assertThat(response.getLastName()).isEqualTo("Фамилия"));
    }

    @ParameterizedTest(name = "{0} = [{1}]")
    @MethodSource("invalidProveFields")
    public void testPostProveVolunteerInIncident_invalidField_response_is400(String field, Object value) {
        Map<String, Object> body = new HashMap<>(Map.of("incNumber", 1, "action", ACTION.PROVE.name()));
        webTestClient.post()
                .uri(BASE_URL + "me/incident/act")
                .header(VolunteerController.USER_HEADER, Base.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(withField(body, field, value))
                .exchange()
                .expectStatus().isBadRequest();
    }

    private static Stream<Arguments> invalidCreateFields() {
        return Stream.of(
                Arguments.of("fio", null),
                Arguments.of("fio", ""),
                Arguments.of("fio", "   "),
                Arguments.of("gender", null),
                Arguments.of("gender", "UNKNOWN"),
                Arguments.of("phoneNumber", null),
                Arguments.of("phoneNumber", ""),
                Arguments.of("phoneNumber", "   "),
                Arguments.of("email", null),
                Arguments.of("email", ""),
                Arguments.of("email", "   "),
                Arguments.of("birthday", null),
                Arguments.of("birthday", "02.01.1993"),
                Arguments.of("city", null),
                Arguments.of("city", ""),
                Arguments.of("city", "   ")
        );
    }

    private static Stream<Arguments> invalidPatchFields() {
        return Stream.of("lastName", "phoneNumber", "email", "city", "restrict")
                .flatMap(field -> Stream.of(Arguments.of(field, ""), Arguments.of(field, "   ")));
    }

    private static Stream<Arguments> invalidProveFields() {
        return Stream.of(
                Arguments.of("incNumber", 0),
                Arguments.of("incNumber", -1),
                Arguments.of("action", null),
                Arguments.of("action", "UNKNOWN")
        );
    }

    private static Map<String, Object> validCreateRequest() {
        return new HashMap<>(Map.of(
                "fio", "Фамилия Имя Отчество",
                "gender", GENDER.MALE.name(),
                "phoneNumber", "79066546654",
                "email", "ya@mail.com",
                "birthday", "1993-01-02",
                "city", "Moscow",
                "restrict", "Мытищи"
        ));
    }

    /**
     * Волонтёр Username из insert-volunteer.sql
     */
    private static void assertIsUsername(VolunteerInfoDto response) {
        assertThat(response.getUserId()).isEqualTo(Base.USERNAME);
        assertThat(response.getLastName()).isEqualTo("Фамилия");
        assertThat(response.getFirstName()).isEqualTo("Имя");
        assertThat(response.getMiddleName()).isEqualTo("Отчество");
        assertThat(response.getStatus()).isEqualTo(STATUS.AVAILABLE);
        assertThat(response.getCreateDate()).isNotNull();
    }

    private Map<CONTACTTYPE, String> getContacts(UUID volunteerId) {
        return em.createQuery("select c from ContactInfo c where c.volunteerId.id = ?1", ContactInfo.class)
                .setParameter(1, volunteerId)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(ContactInfo::getContactType, ContactInfo::getContact));
    }

    /**
     * null удаляет поле из тела, иначе подменяет значение
     */
    private static Map<String, Object> withField(Map<String, Object> body, String field, Object value) {
        if (value == null) {
            body.remove(field);
        } else {
            body.put(field, value);
        }
        return body;
    }
}
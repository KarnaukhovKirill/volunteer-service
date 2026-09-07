package ru.kirill.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
@SpringBootTest
class VolunteerControllerTest {

    public static final String BASE_URL = "/api/v1/volunteer/";
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testCreateVolunteer_response_is201() {
        webTestClient.post()
                .uri(BASE_URL + "register/me")
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
                .expectBody(VolunteerInfo.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getId()).isGreaterThanOrEqualTo(0);
                });
    }

    @Test
    public void testCreateVolunteer_response_is4xx() {
        webTestClient.post()
                .uri(BASE_URL + "register/me")
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
                .header(VolunteerController.USER_HEADER, "Username")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VolunteerInfo.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getId()).isGreaterThanOrEqualTo(0);
                });
    }

    @Test
    public void testDeleteVolunteer_response_success() {
        webTestClient.delete()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, "Username")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void testPatchVolunteer_response_success() {
        webTestClient.patch()
                .uri(BASE_URL + "me")
                .header(VolunteerController.USER_HEADER, "Username")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VolunteerInfo.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getId()).isGreaterThanOrEqualTo(0);
                });
    }

    @Test
    public void testPostListVolunteer_response_success() {
        webTestClient.post()
                .uri(BASE_URL + "/list")
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
                .uri(BASE_URL + "1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VolunteerInfo.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getId()).isGreaterThanOrEqualTo(0);
                });
    }

    @Test
    public void testPostVolunteersByIds_response_success() {
        webTestClient.post()
                .uri("/internal/api/v1/volunteer/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(1,2,3))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ContactsVolunteerResponse.class)
                .value(response ->
                        assertThat(response.getVolunteerContacts().size()).isEqualTo(1));
    }

    @Test
    public void testPostProveVolunteerInIncident_response_success() {
        webTestClient.post()
                .uri(BASE_URL + "/me/incident/act")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProveVolunteer.builder().incNumber(1).action(ACTION.PROVE).build())
                .header(VolunteerController.USER_HEADER, "Username")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void testPostProveVolunteerInIncident_response_is4xx() {
        webTestClient.post()
                .uri(BASE_URL + "/me/incident/act")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ProveVolunteer.builder().incNumber(1).build())
                .header(VolunteerController.USER_HEADER, "Username")
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
package com.reactnatjhip.developer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.reactnatjhip.developer.IntegrationTest;
import com.reactnatjhip.developer.domain.Appointment;
import com.reactnatjhip.developer.repository.AppointmentRepository;
import com.reactnatjhip.developer.service.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link AppointmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class AppointmentResourceIT {

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_STATE = false;
    private static final Boolean UPDATED_STATE = true;

    private static final String DEFAULT_REPORTREASON = "AAAAAAAAAA";
    private static final String UPDATED_REPORTREASON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/appointments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Appointment appointment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointment createEntity(EntityManager em) {
        Appointment appointment = new Appointment()
            .reason(DEFAULT_REASON)
            .date(DEFAULT_DATE)
            .state(DEFAULT_STATE)
            .reportreason(DEFAULT_REPORTREASON);
        return appointment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointment createUpdatedEntity(EntityManager em) {
        Appointment appointment = new Appointment()
            .reason(UPDATED_REASON)
            .date(UPDATED_DATE)
            .state(UPDATED_STATE)
            .reportreason(UPDATED_REPORTREASON);
        return appointment;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Appointment.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        appointment = createEntity(em);
    }

    @Test
    void createAppointment() throws Exception {
        int databaseSizeBeforeCreate = appointmentRepository.findAll().collectList().block().size();
        // Create the Appointment
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appointment))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate + 1);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getReason()).isEqualTo(DEFAULT_REASON);
        assertThat(testAppointment.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testAppointment.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testAppointment.getReportreason()).isEqualTo(DEFAULT_REPORTREASON);
    }

    @Test
    void createAppointmentWithExistingId() throws Exception {
        // Create the Appointment with an existing ID
        appointment.setId(1L);

        int databaseSizeBeforeCreate = appointmentRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appointment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAppointments() {
        // Initialize the database
        appointmentRepository.save(appointment).block();

        // Get all the appointmentList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(appointment.getId().intValue()))
            .jsonPath("$.[*].reason")
            .value(hasItem(DEFAULT_REASON))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].state")
            .value(hasItem(DEFAULT_STATE.booleanValue()))
            .jsonPath("$.[*].reportreason")
            .value(hasItem(DEFAULT_REPORTREASON));
    }

    @Test
    void getAppointment() {
        // Initialize the database
        appointmentRepository.save(appointment).block();

        // Get the appointment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, appointment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(appointment.getId().intValue()))
            .jsonPath("$.reason")
            .value(is(DEFAULT_REASON))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.state")
            .value(is(DEFAULT_STATE.booleanValue()))
            .jsonPath("$.reportreason")
            .value(is(DEFAULT_REPORTREASON));
    }

    @Test
    void getNonExistingAppointment() {
        // Get the appointment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.save(appointment).block();

        int databaseSizeBeforeUpdate = appointmentRepository.findAll().collectList().block().size();

        // Update the appointment
        Appointment updatedAppointment = appointmentRepository.findById(appointment.getId()).block();
        updatedAppointment.reason(UPDATED_REASON).date(UPDATED_DATE).state(UPDATED_STATE).reportreason(UPDATED_REPORTREASON);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAppointment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedAppointment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testAppointment.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testAppointment.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testAppointment.getReportreason()).isEqualTo(UPDATED_REPORTREASON);
    }

    @Test
    void putNonExistingAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().collectList().block().size();
        appointment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appointment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appointment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().collectList().block().size();
        appointment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appointment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().collectList().block().size();
        appointment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appointment))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAppointmentWithPatch() throws Exception {
        // Initialize the database
        appointmentRepository.save(appointment).block();

        int databaseSizeBeforeUpdate = appointmentRepository.findAll().collectList().block().size();

        // Update the appointment using partial update
        Appointment partialUpdatedAppointment = new Appointment();
        partialUpdatedAppointment.setId(appointment.getId());

        partialUpdatedAppointment.reason(UPDATED_REASON).date(UPDATED_DATE).state(UPDATED_STATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppointment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAppointment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testAppointment.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testAppointment.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testAppointment.getReportreason()).isEqualTo(DEFAULT_REPORTREASON);
    }

    @Test
    void fullUpdateAppointmentWithPatch() throws Exception {
        // Initialize the database
        appointmentRepository.save(appointment).block();

        int databaseSizeBeforeUpdate = appointmentRepository.findAll().collectList().block().size();

        // Update the appointment using partial update
        Appointment partialUpdatedAppointment = new Appointment();
        partialUpdatedAppointment.setId(appointment.getId());

        partialUpdatedAppointment.reason(UPDATED_REASON).date(UPDATED_DATE).state(UPDATED_STATE).reportreason(UPDATED_REPORTREASON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppointment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAppointment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testAppointment.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testAppointment.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testAppointment.getReportreason()).isEqualTo(UPDATED_REPORTREASON);
    }

    @Test
    void patchNonExistingAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().collectList().block().size();
        appointment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, appointment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appointment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().collectList().block().size();
        appointment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appointment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().collectList().block().size();
        appointment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appointment))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAppointment() {
        // Initialize the database
        appointmentRepository.save(appointment).block();

        int databaseSizeBeforeDelete = appointmentRepository.findAll().collectList().block().size();

        // Delete the appointment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, appointment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Appointment> appointmentList = appointmentRepository.findAll().collectList().block();
        assertThat(appointmentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

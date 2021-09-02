package com.reactnatjhip.developer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.reactnatjhip.developer.IntegrationTest;
import com.reactnatjhip.developer.domain.Adviser;
import com.reactnatjhip.developer.repository.AdviserRepository;
import com.reactnatjhip.developer.service.EntityManager;
import java.time.Duration;
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
 * Integration tests for the {@link AdviserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class AdviserResourceIT {

    private static final String DEFAULT_REGISTRATION_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REGISTRATION_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY = "BBBBBBBBBB";

    private static final String DEFAULT_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/advisers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AdviserRepository adviserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Adviser adviser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Adviser createEntity(EntityManager em) {
        Adviser adviser = new Adviser()
            .registrationNumber(DEFAULT_REGISTRATION_NUMBER)
            .company(DEFAULT_COMPANY)
            .department(DEFAULT_DEPARTMENT);
        return adviser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Adviser createUpdatedEntity(EntityManager em) {
        Adviser adviser = new Adviser()
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .company(UPDATED_COMPANY)
            .department(UPDATED_DEPARTMENT);
        return adviser;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Adviser.class).block();
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
        adviser = createEntity(em);
    }

    @Test
    void createAdviser() throws Exception {
        int databaseSizeBeforeCreate = adviserRepository.findAll().collectList().block().size();
        // Create the Adviser
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(adviser))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeCreate + 1);
        Adviser testAdviser = adviserList.get(adviserList.size() - 1);
        assertThat(testAdviser.getRegistrationNumber()).isEqualTo(DEFAULT_REGISTRATION_NUMBER);
        assertThat(testAdviser.getCompany()).isEqualTo(DEFAULT_COMPANY);
        assertThat(testAdviser.getDepartment()).isEqualTo(DEFAULT_DEPARTMENT);
    }

    @Test
    void createAdviserWithExistingId() throws Exception {
        // Create the Adviser with an existing ID
        adviser.setId(1L);

        int databaseSizeBeforeCreate = adviserRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(adviser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAdvisers() {
        // Initialize the database
        adviserRepository.save(adviser).block();

        // Get all the adviserList
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
            .value(hasItem(adviser.getId().intValue()))
            .jsonPath("$.[*].registrationNumber")
            .value(hasItem(DEFAULT_REGISTRATION_NUMBER))
            .jsonPath("$.[*].company")
            .value(hasItem(DEFAULT_COMPANY))
            .jsonPath("$.[*].department")
            .value(hasItem(DEFAULT_DEPARTMENT));
    }

    @Test
    void getAdviser() {
        // Initialize the database
        adviserRepository.save(adviser).block();

        // Get the adviser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, adviser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(adviser.getId().intValue()))
            .jsonPath("$.registrationNumber")
            .value(is(DEFAULT_REGISTRATION_NUMBER))
            .jsonPath("$.company")
            .value(is(DEFAULT_COMPANY))
            .jsonPath("$.department")
            .value(is(DEFAULT_DEPARTMENT));
    }

    @Test
    void getNonExistingAdviser() {
        // Get the adviser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewAdviser() throws Exception {
        // Initialize the database
        adviserRepository.save(adviser).block();

        int databaseSizeBeforeUpdate = adviserRepository.findAll().collectList().block().size();

        // Update the adviser
        Adviser updatedAdviser = adviserRepository.findById(adviser.getId()).block();
        updatedAdviser.registrationNumber(UPDATED_REGISTRATION_NUMBER).company(UPDATED_COMPANY).department(UPDATED_DEPARTMENT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAdviser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedAdviser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
        Adviser testAdviser = adviserList.get(adviserList.size() - 1);
        assertThat(testAdviser.getRegistrationNumber()).isEqualTo(UPDATED_REGISTRATION_NUMBER);
        assertThat(testAdviser.getCompany()).isEqualTo(UPDATED_COMPANY);
        assertThat(testAdviser.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
    }

    @Test
    void putNonExistingAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().collectList().block().size();
        adviser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, adviser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(adviser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().collectList().block().size();
        adviser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(adviser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().collectList().block().size();
        adviser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(adviser))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAdviserWithPatch() throws Exception {
        // Initialize the database
        adviserRepository.save(adviser).block();

        int databaseSizeBeforeUpdate = adviserRepository.findAll().collectList().block().size();

        // Update the adviser using partial update
        Adviser partialUpdatedAdviser = new Adviser();
        partialUpdatedAdviser.setId(adviser.getId());

        partialUpdatedAdviser.registrationNumber(UPDATED_REGISTRATION_NUMBER).company(UPDATED_COMPANY).department(UPDATED_DEPARTMENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAdviser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAdviser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
        Adviser testAdviser = adviserList.get(adviserList.size() - 1);
        assertThat(testAdviser.getRegistrationNumber()).isEqualTo(UPDATED_REGISTRATION_NUMBER);
        assertThat(testAdviser.getCompany()).isEqualTo(UPDATED_COMPANY);
        assertThat(testAdviser.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
    }

    @Test
    void fullUpdateAdviserWithPatch() throws Exception {
        // Initialize the database
        adviserRepository.save(adviser).block();

        int databaseSizeBeforeUpdate = adviserRepository.findAll().collectList().block().size();

        // Update the adviser using partial update
        Adviser partialUpdatedAdviser = new Adviser();
        partialUpdatedAdviser.setId(adviser.getId());

        partialUpdatedAdviser.registrationNumber(UPDATED_REGISTRATION_NUMBER).company(UPDATED_COMPANY).department(UPDATED_DEPARTMENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAdviser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAdviser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
        Adviser testAdviser = adviserList.get(adviserList.size() - 1);
        assertThat(testAdviser.getRegistrationNumber()).isEqualTo(UPDATED_REGISTRATION_NUMBER);
        assertThat(testAdviser.getCompany()).isEqualTo(UPDATED_COMPANY);
        assertThat(testAdviser.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
    }

    @Test
    void patchNonExistingAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().collectList().block().size();
        adviser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, adviser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(adviser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().collectList().block().size();
        adviser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(adviser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().collectList().block().size();
        adviser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(adviser))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAdviser() {
        // Initialize the database
        adviserRepository.save(adviser).block();

        int databaseSizeBeforeDelete = adviserRepository.findAll().collectList().block().size();

        // Delete the adviser
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, adviser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Adviser> adviserList = adviserRepository.findAll().collectList().block();
        assertThat(adviserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

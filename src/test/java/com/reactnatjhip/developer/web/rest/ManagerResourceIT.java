package com.reactnatjhip.developer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.reactnatjhip.developer.IntegrationTest;
import com.reactnatjhip.developer.domain.Manager;
import com.reactnatjhip.developer.repository.ManagerRepository;
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
 * Integration tests for the {@link ManagerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class ManagerResourceIT {

    private static final String DEFAULT_REGISTRATION_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REGISTRATION_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/managers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Manager manager;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Manager createEntity(EntityManager em) {
        Manager manager = new Manager().registrationNumber(DEFAULT_REGISTRATION_NUMBER).department(DEFAULT_DEPARTMENT);
        return manager;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Manager createUpdatedEntity(EntityManager em) {
        Manager manager = new Manager().registrationNumber(UPDATED_REGISTRATION_NUMBER).department(UPDATED_DEPARTMENT);
        return manager;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Manager.class).block();
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
        manager = createEntity(em);
    }

    @Test
    void createManager() throws Exception {
        int databaseSizeBeforeCreate = managerRepository.findAll().collectList().block().size();
        // Create the Manager
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(manager))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeCreate + 1);
        Manager testManager = managerList.get(managerList.size() - 1);
        assertThat(testManager.getRegistrationNumber()).isEqualTo(DEFAULT_REGISTRATION_NUMBER);
        assertThat(testManager.getDepartment()).isEqualTo(DEFAULT_DEPARTMENT);
    }

    @Test
    void createManagerWithExistingId() throws Exception {
        // Create the Manager with an existing ID
        manager.setId(1L);

        int databaseSizeBeforeCreate = managerRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(manager))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllManagers() {
        // Initialize the database
        managerRepository.save(manager).block();

        // Get all the managerList
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
            .value(hasItem(manager.getId().intValue()))
            .jsonPath("$.[*].registrationNumber")
            .value(hasItem(DEFAULT_REGISTRATION_NUMBER))
            .jsonPath("$.[*].department")
            .value(hasItem(DEFAULT_DEPARTMENT));
    }

    @Test
    void getManager() {
        // Initialize the database
        managerRepository.save(manager).block();

        // Get the manager
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, manager.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(manager.getId().intValue()))
            .jsonPath("$.registrationNumber")
            .value(is(DEFAULT_REGISTRATION_NUMBER))
            .jsonPath("$.department")
            .value(is(DEFAULT_DEPARTMENT));
    }

    @Test
    void getNonExistingManager() {
        // Get the manager
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewManager() throws Exception {
        // Initialize the database
        managerRepository.save(manager).block();

        int databaseSizeBeforeUpdate = managerRepository.findAll().collectList().block().size();

        // Update the manager
        Manager updatedManager = managerRepository.findById(manager.getId()).block();
        updatedManager.registrationNumber(UPDATED_REGISTRATION_NUMBER).department(UPDATED_DEPARTMENT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedManager.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedManager))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
        Manager testManager = managerList.get(managerList.size() - 1);
        assertThat(testManager.getRegistrationNumber()).isEqualTo(UPDATED_REGISTRATION_NUMBER);
        assertThat(testManager.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
    }

    @Test
    void putNonExistingManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().collectList().block().size();
        manager.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, manager.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(manager))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().collectList().block().size();
        manager.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(manager))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().collectList().block().size();
        manager.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(manager))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateManagerWithPatch() throws Exception {
        // Initialize the database
        managerRepository.save(manager).block();

        int databaseSizeBeforeUpdate = managerRepository.findAll().collectList().block().size();

        // Update the manager using partial update
        Manager partialUpdatedManager = new Manager();
        partialUpdatedManager.setId(manager.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedManager.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedManager))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
        Manager testManager = managerList.get(managerList.size() - 1);
        assertThat(testManager.getRegistrationNumber()).isEqualTo(DEFAULT_REGISTRATION_NUMBER);
        assertThat(testManager.getDepartment()).isEqualTo(DEFAULT_DEPARTMENT);
    }

    @Test
    void fullUpdateManagerWithPatch() throws Exception {
        // Initialize the database
        managerRepository.save(manager).block();

        int databaseSizeBeforeUpdate = managerRepository.findAll().collectList().block().size();

        // Update the manager using partial update
        Manager partialUpdatedManager = new Manager();
        partialUpdatedManager.setId(manager.getId());

        partialUpdatedManager.registrationNumber(UPDATED_REGISTRATION_NUMBER).department(UPDATED_DEPARTMENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedManager.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedManager))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
        Manager testManager = managerList.get(managerList.size() - 1);
        assertThat(testManager.getRegistrationNumber()).isEqualTo(UPDATED_REGISTRATION_NUMBER);
        assertThat(testManager.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
    }

    @Test
    void patchNonExistingManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().collectList().block().size();
        manager.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, manager.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(manager))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().collectList().block().size();
        manager.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(manager))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().collectList().block().size();
        manager.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(manager))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteManager() {
        // Initialize the database
        managerRepository.save(manager).block();

        int databaseSizeBeforeDelete = managerRepository.findAll().collectList().block().size();

        // Delete the manager
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, manager.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Manager> managerList = managerRepository.findAll().collectList().block();
        assertThat(managerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

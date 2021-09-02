package com.reactnatjhip.developer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.reactnatjhip.developer.IntegrationTest;
import com.reactnatjhip.developer.domain.Bank;
import com.reactnatjhip.developer.repository.BankRepository;
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
 * Integration tests for the {@link BankResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class BankResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/banks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Bank bank;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bank createEntity(EntityManager em) {
        Bank bank = new Bank().name(DEFAULT_NAME).address(DEFAULT_ADDRESS);
        return bank;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bank createUpdatedEntity(EntityManager em) {
        Bank bank = new Bank().name(UPDATED_NAME).address(UPDATED_ADDRESS);
        return bank;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Bank.class).block();
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
        bank = createEntity(em);
    }

    @Test
    void createBank() throws Exception {
        int databaseSizeBeforeCreate = bankRepository.findAll().collectList().block().size();
        // Create the Bank
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bank))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Bank in the database
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeCreate + 1);
        Bank testBank = bankList.get(bankList.size() - 1);
        assertThat(testBank.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBank.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    void createBankWithExistingId() throws Exception {
        // Create the Bank with an existing ID
        bank.setId(1L);

        int databaseSizeBeforeCreate = bankRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bank))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bank in the database
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllBanksAsStream() {
        // Initialize the database
        bankRepository.save(bank).block();

        List<Bank> bankList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Bank.class)
            .getResponseBody()
            .filter(bank::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(bankList).isNotNull();
        assertThat(bankList).hasSize(1);
        Bank testBank = bankList.get(0);
        assertThat(testBank.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBank.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    void getAllBanks() {
        // Initialize the database
        bankRepository.save(bank).block();

        // Get all the bankList
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
            .value(hasItem(bank.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS));
    }

    @Test
    void getBank() {
        // Initialize the database
        bankRepository.save(bank).block();

        // Get the bank
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, bank.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(bank.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS));
    }

    @Test
    void getNonExistingBank() {
        // Get the bank
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewBank() throws Exception {
        // Initialize the database
        bankRepository.save(bank).block();

        int databaseSizeBeforeUpdate = bankRepository.findAll().collectList().block().size();

        // Update the bank
        Bank updatedBank = bankRepository.findById(bank.getId()).block();
        updatedBank.name(UPDATED_NAME).address(UPDATED_ADDRESS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedBank.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedBank))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bank in the database
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeUpdate);
        Bank testBank = bankList.get(bankList.size() - 1);
        assertThat(testBank.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBank.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    void putNonExistingBank() throws Exception {
        int databaseSizeBeforeUpdate = bankRepository.findAll().collectList().block().size();
        bank.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, bank.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bank))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bank in the database
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBank() throws Exception {
        int databaseSizeBeforeUpdate = bankRepository.findAll().collectList().block().size();
        bank.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bank))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bank in the database
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBank() throws Exception {
        int databaseSizeBeforeUpdate = bankRepository.findAll().collectList().block().size();
        bank.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bank))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bank in the database
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBankWithPatch() throws Exception {
        // Initialize the database
        bankRepository.save(bank).block();

        int databaseSizeBeforeUpdate = bankRepository.findAll().collectList().block().size();

        // Update the bank using partial update
        Bank partialUpdatedBank = new Bank();
        partialUpdatedBank.setId(bank.getId());

        partialUpdatedBank.name(UPDATED_NAME).address(UPDATED_ADDRESS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBank.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBank))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bank in the database
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeUpdate);
        Bank testBank = bankList.get(bankList.size() - 1);
        assertThat(testBank.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBank.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    void fullUpdateBankWithPatch() throws Exception {
        // Initialize the database
        bankRepository.save(bank).block();

        int databaseSizeBeforeUpdate = bankRepository.findAll().collectList().block().size();

        // Update the bank using partial update
        Bank partialUpdatedBank = new Bank();
        partialUpdatedBank.setId(bank.getId());

        partialUpdatedBank.name(UPDATED_NAME).address(UPDATED_ADDRESS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBank.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBank))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bank in the database
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeUpdate);
        Bank testBank = bankList.get(bankList.size() - 1);
        assertThat(testBank.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBank.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    void patchNonExistingBank() throws Exception {
        int databaseSizeBeforeUpdate = bankRepository.findAll().collectList().block().size();
        bank.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, bank.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bank))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bank in the database
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBank() throws Exception {
        int databaseSizeBeforeUpdate = bankRepository.findAll().collectList().block().size();
        bank.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bank))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bank in the database
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBank() throws Exception {
        int databaseSizeBeforeUpdate = bankRepository.findAll().collectList().block().size();
        bank.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bank))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bank in the database
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBank() {
        // Initialize the database
        bankRepository.save(bank).block();

        int databaseSizeBeforeDelete = bankRepository.findAll().collectList().block().size();

        // Delete the bank
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, bank.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Bank> bankList = bankRepository.findAll().collectList().block();
        assertThat(bankList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

package com.reactnatjhip.developer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.reactnatjhip.developer.IntegrationTest;
import com.reactnatjhip.developer.domain.Company;
import com.reactnatjhip.developer.repository.CompanyRepository;
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
 * Integration tests for the {@link CompanyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CompanyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NINEA = "AAAAAAAAAA";
    private static final String UPDATED_NINEA = "BBBBBBBBBB";

    private static final String DEFAULT_RC = "AAAAAAAAAA";
    private static final String UPDATED_RC = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/companies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Company company;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Company createEntity(EntityManager em) {
        Company company = new Company().name(DEFAULT_NAME).ninea(DEFAULT_NINEA).rc(DEFAULT_RC).address(DEFAULT_ADDRESS);
        return company;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Company createUpdatedEntity(EntityManager em) {
        Company company = new Company().name(UPDATED_NAME).ninea(UPDATED_NINEA).rc(UPDATED_RC).address(UPDATED_ADDRESS);
        return company;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Company.class).block();
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
        company = createEntity(em);
    }

    @Test
    void createCompany() throws Exception {
        int databaseSizeBeforeCreate = companyRepository.findAll().collectList().block().size();
        // Create the Company
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeCreate + 1);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCompany.getNinea()).isEqualTo(DEFAULT_NINEA);
        assertThat(testCompany.getRc()).isEqualTo(DEFAULT_RC);
        assertThat(testCompany.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    void createCompanyWithExistingId() throws Exception {
        // Create the Company with an existing ID
        company.setId(1L);

        int databaseSizeBeforeCreate = companyRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCompaniesAsStream() {
        // Initialize the database
        companyRepository.save(company).block();

        List<Company> companyList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Company.class)
            .getResponseBody()
            .filter(company::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(companyList).isNotNull();
        assertThat(companyList).hasSize(1);
        Company testCompany = companyList.get(0);
        assertThat(testCompany.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCompany.getNinea()).isEqualTo(DEFAULT_NINEA);
        assertThat(testCompany.getRc()).isEqualTo(DEFAULT_RC);
        assertThat(testCompany.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    void getAllCompanies() {
        // Initialize the database
        companyRepository.save(company).block();

        // Get all the companyList
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
            .value(hasItem(company.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].ninea")
            .value(hasItem(DEFAULT_NINEA))
            .jsonPath("$.[*].rc")
            .value(hasItem(DEFAULT_RC))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS));
    }

    @Test
    void getCompany() {
        // Initialize the database
        companyRepository.save(company).block();

        // Get the company
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, company.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(company.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.ninea")
            .value(is(DEFAULT_NINEA))
            .jsonPath("$.rc")
            .value(is(DEFAULT_RC))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS));
    }

    @Test
    void getNonExistingCompany() {
        // Get the company
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCompany() throws Exception {
        // Initialize the database
        companyRepository.save(company).block();

        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();

        // Update the company
        Company updatedCompany = companyRepository.findById(company.getId()).block();
        updatedCompany.name(UPDATED_NAME).ninea(UPDATED_NINEA).rc(UPDATED_RC).address(UPDATED_ADDRESS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCompany.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCompany))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompany.getNinea()).isEqualTo(UPDATED_NINEA);
        assertThat(testCompany.getRc()).isEqualTo(UPDATED_RC);
        assertThat(testCompany.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    void putNonExistingCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, company.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCompanyWithPatch() throws Exception {
        // Initialize the database
        companyRepository.save(company).block();

        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();

        // Update the company using partial update
        Company partialUpdatedCompany = new Company();
        partialUpdatedCompany.setId(company.getId());

        partialUpdatedCompany.ninea(UPDATED_NINEA).rc(UPDATED_RC);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCompany.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCompany))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCompany.getNinea()).isEqualTo(UPDATED_NINEA);
        assertThat(testCompany.getRc()).isEqualTo(UPDATED_RC);
        assertThat(testCompany.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    void fullUpdateCompanyWithPatch() throws Exception {
        // Initialize the database
        companyRepository.save(company).block();

        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();

        // Update the company using partial update
        Company partialUpdatedCompany = new Company();
        partialUpdatedCompany.setId(company.getId());

        partialUpdatedCompany.name(UPDATED_NAME).ninea(UPDATED_NINEA).rc(UPDATED_RC).address(UPDATED_ADDRESS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCompany.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCompany))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompany.getNinea()).isEqualTo(UPDATED_NINEA);
        assertThat(testCompany.getRc()).isEqualTo(UPDATED_RC);
        assertThat(testCompany.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    void patchNonExistingCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, company.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCompany() {
        // Initialize the database
        companyRepository.save(company).block();

        int databaseSizeBeforeDelete = companyRepository.findAll().collectList().block().size();

        // Delete the company
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, company.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

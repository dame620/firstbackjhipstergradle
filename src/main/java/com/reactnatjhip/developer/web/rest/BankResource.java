package com.reactnatjhip.developer.web.rest;

import com.reactnatjhip.developer.domain.Bank;
import com.reactnatjhip.developer.repository.BankRepository;
import com.reactnatjhip.developer.service.BankService;
import com.reactnatjhip.developer.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.reactnatjhip.developer.domain.Bank}.
 */
@RestController
@RequestMapping("/api")
public class BankResource {

    private final Logger log = LoggerFactory.getLogger(BankResource.class);

    private static final String ENTITY_NAME = "bank";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BankService bankService;

    private final BankRepository bankRepository;

    public BankResource(BankService bankService, BankRepository bankRepository) {
        this.bankService = bankService;
        this.bankRepository = bankRepository;
    }

    /**
     * {@code POST  /banks} : Create a new bank.
     *
     * @param bank the bank to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bank, or with status {@code 400 (Bad Request)} if the bank has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/banks")
    public Mono<ResponseEntity<Bank>> createBank(@RequestBody Bank bank) throws URISyntaxException {
        log.debug("REST request to save Bank : {}", bank);
        if (bank.getId() != null) {
            throw new BadRequestAlertException("A new bank cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return bankService
            .save(bank)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/banks/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /banks/:id} : Updates an existing bank.
     *
     * @param id the id of the bank to save.
     * @param bank the bank to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bank,
     * or with status {@code 400 (Bad Request)} if the bank is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bank couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/banks/{id}")
    public Mono<ResponseEntity<Bank>> updateBank(@PathVariable(value = "id", required = false) final Long id, @RequestBody Bank bank)
        throws URISyntaxException {
        log.debug("REST request to update Bank : {}, {}", id, bank);
        if (bank.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bank.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return bankRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return bankService
                        .save(bank)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /banks/:id} : Partial updates given fields of an existing bank, field will ignore if it is null
     *
     * @param id the id of the bank to save.
     * @param bank the bank to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bank,
     * or with status {@code 400 (Bad Request)} if the bank is not valid,
     * or with status {@code 404 (Not Found)} if the bank is not found,
     * or with status {@code 500 (Internal Server Error)} if the bank couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/banks/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Bank>> partialUpdateBank(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Bank bank
    ) throws URISyntaxException {
        log.debug("REST request to partial update Bank partially : {}, {}", id, bank);
        if (bank.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bank.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return bankRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Bank> result = bankService.partialUpdate(bank);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /banks} : get all the banks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of banks in body.
     */
    @GetMapping("/banks")
    public Mono<List<Bank>> getAllBanks() {
        log.debug("REST request to get all Banks");
        return bankService.findAll().collectList();
    }

    /**
     * {@code GET  /banks} : get all the banks as a stream.
     * @return the {@link Flux} of banks.
     */
    @GetMapping(value = "/banks", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Bank> getAllBanksAsStream() {
        log.debug("REST request to get all Banks as a stream");
        return bankService.findAll();
    }

    /**
     * {@code GET  /banks/:id} : get the "id" bank.
     *
     * @param id the id of the bank to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bank, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/banks/{id}")
    public Mono<ResponseEntity<Bank>> getBank(@PathVariable Long id) {
        log.debug("REST request to get Bank : {}", id);
        Mono<Bank> bank = bankService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bank);
    }

    /**
     * {@code DELETE  /banks/:id} : delete the "id" bank.
     *
     * @param id the id of the bank to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/banks/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteBank(@PathVariable Long id) {
        log.debug("REST request to delete Bank : {}", id);
        return bankService
            .delete(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}

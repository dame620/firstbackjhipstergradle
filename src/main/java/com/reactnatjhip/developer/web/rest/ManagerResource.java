package com.reactnatjhip.developer.web.rest;

import com.reactnatjhip.developer.domain.Manager;
import com.reactnatjhip.developer.repository.ManagerRepository;
import com.reactnatjhip.developer.service.ManagerService;
import com.reactnatjhip.developer.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.reactnatjhip.developer.domain.Manager}.
 */
@RestController
@RequestMapping("/api")
public class ManagerResource {

    private final Logger log = LoggerFactory.getLogger(ManagerResource.class);

    private static final String ENTITY_NAME = "manager";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ManagerService managerService;

    private final ManagerRepository managerRepository;

    public ManagerResource(ManagerService managerService, ManagerRepository managerRepository) {
        this.managerService = managerService;
        this.managerRepository = managerRepository;
    }

    /**
     * {@code POST  /managers} : Create a new manager.
     *
     * @param manager the manager to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new manager, or with status {@code 400 (Bad Request)} if the manager has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/managers")
    public Mono<ResponseEntity<Manager>> createManager(@RequestBody Manager manager) throws URISyntaxException {
        log.debug("REST request to save Manager : {}", manager);
        if (manager.getId() != null) {
            throw new BadRequestAlertException("A new manager cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return managerService
            .save(manager)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/managers/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /managers/:id} : Updates an existing manager.
     *
     * @param id the id of the manager to save.
     * @param manager the manager to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated manager,
     * or with status {@code 400 (Bad Request)} if the manager is not valid,
     * or with status {@code 500 (Internal Server Error)} if the manager couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/managers/{id}")
    public Mono<ResponseEntity<Manager>> updateManager(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Manager manager
    ) throws URISyntaxException {
        log.debug("REST request to update Manager : {}, {}", id, manager);
        if (manager.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, manager.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return managerRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return managerService
                        .save(manager)
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
     * {@code PATCH  /managers/:id} : Partial updates given fields of an existing manager, field will ignore if it is null
     *
     * @param id the id of the manager to save.
     * @param manager the manager to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated manager,
     * or with status {@code 400 (Bad Request)} if the manager is not valid,
     * or with status {@code 404 (Not Found)} if the manager is not found,
     * or with status {@code 500 (Internal Server Error)} if the manager couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/managers/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Manager>> partialUpdateManager(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Manager manager
    ) throws URISyntaxException {
        log.debug("REST request to partial update Manager partially : {}, {}", id, manager);
        if (manager.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, manager.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return managerRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Manager> result = managerService.partialUpdate(manager);

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
     * {@code GET  /managers} : get all the managers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of managers in body.
     */
    @GetMapping("/managers")
    public Mono<ResponseEntity<List<Manager>>> getAllManagers(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Managers");
        return managerService
            .countAll()
            .zipWith(managerService.findAll(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /managers/:id} : get the "id" manager.
     *
     * @param id the id of the manager to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the manager, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/managers/{id}")
    public Mono<ResponseEntity<Manager>> getManager(@PathVariable Long id) {
        log.debug("REST request to get Manager : {}", id);
        Mono<Manager> manager = managerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(manager);
    }

    /**
     * {@code DELETE  /managers/:id} : delete the "id" manager.
     *
     * @param id the id of the manager to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/managers/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteManager(@PathVariable Long id) {
        log.debug("REST request to delete Manager : {}", id);
        return managerService
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

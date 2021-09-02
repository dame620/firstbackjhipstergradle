package com.reactnatjhip.developer.web.rest;

import com.reactnatjhip.developer.domain.Appointment;
import com.reactnatjhip.developer.repository.AppointmentRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.reactnatjhip.developer.domain.Appointment}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AppointmentResource {

    private final Logger log = LoggerFactory.getLogger(AppointmentResource.class);

    private static final String ENTITY_NAME = "appointment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppointmentRepository appointmentRepository;

    public AppointmentResource(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * {@code POST  /appointments} : Create a new appointment.
     *
     * @param appointment the appointment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appointment, or with status {@code 400 (Bad Request)} if the appointment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/appointments")
    public Mono<ResponseEntity<Appointment>> createAppointment(@RequestBody Appointment appointment) throws URISyntaxException {
        log.debug("REST request to save Appointment : {}", appointment);
        if (appointment.getId() != null) {
            throw new BadRequestAlertException("A new appointment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return appointmentRepository
            .save(appointment)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/appointments/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /appointments/:id} : Updates an existing appointment.
     *
     * @param id the id of the appointment to save.
     * @param appointment the appointment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appointment,
     * or with status {@code 400 (Bad Request)} if the appointment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appointment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/appointments/{id}")
    public Mono<ResponseEntity<Appointment>> updateAppointment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Appointment appointment
    ) throws URISyntaxException {
        log.debug("REST request to update Appointment : {}, {}", id, appointment);
        if (appointment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appointment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appointmentRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return appointmentRepository
                        .save(appointment)
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
     * {@code PATCH  /appointments/:id} : Partial updates given fields of an existing appointment, field will ignore if it is null
     *
     * @param id the id of the appointment to save.
     * @param appointment the appointment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appointment,
     * or with status {@code 400 (Bad Request)} if the appointment is not valid,
     * or with status {@code 404 (Not Found)} if the appointment is not found,
     * or with status {@code 500 (Internal Server Error)} if the appointment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/appointments/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Appointment>> partialUpdateAppointment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Appointment appointment
    ) throws URISyntaxException {
        log.debug("REST request to partial update Appointment partially : {}, {}", id, appointment);
        if (appointment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appointment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appointmentRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Appointment> result = appointmentRepository
                        .findById(appointment.getId())
                        .map(
                            existingAppointment -> {
                                if (appointment.getReason() != null) {
                                    existingAppointment.setReason(appointment.getReason());
                                }
                                if (appointment.getDate() != null) {
                                    existingAppointment.setDate(appointment.getDate());
                                }
                                if (appointment.getState() != null) {
                                    existingAppointment.setState(appointment.getState());
                                }
                                if (appointment.getReportreason() != null) {
                                    existingAppointment.setReportreason(appointment.getReportreason());
                                }

                                return existingAppointment;
                            }
                        )
                        .flatMap(appointmentRepository::save);

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
     * {@code GET  /appointments} : get all the appointments.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appointments in body.
     */
    @GetMapping("/appointments")
    public Mono<ResponseEntity<List<Appointment>>> getAllAppointments(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Appointments");
        return appointmentRepository
            .count()
            .zipWith(appointmentRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /appointments/:id} : get the "id" appointment.
     *
     * @param id the id of the appointment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appointment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/appointments/{id}")
    public Mono<ResponseEntity<Appointment>> getAppointment(@PathVariable Long id) {
        log.debug("REST request to get Appointment : {}", id);
        Mono<Appointment> appointment = appointmentRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(appointment);
    }

    /**
     * {@code DELETE  /appointments/:id} : delete the "id" appointment.
     *
     * @param id the id of the appointment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/appointments/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteAppointment(@PathVariable Long id) {
        log.debug("REST request to delete Appointment : {}", id);
        return appointmentRepository
            .deleteById(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}

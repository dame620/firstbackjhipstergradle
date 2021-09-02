package com.reactnatjhip.developer.repository;

import com.reactnatjhip.developer.domain.Appointment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Appointment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppointmentRepository extends R2dbcRepository<Appointment, Long>, AppointmentRepositoryInternal {
    Flux<Appointment> findAllBy(Pageable pageable);

    @Query("SELECT * FROM appointment entity WHERE entity.adviser_id = :id")
    Flux<Appointment> findByAdviser(Long id);

    @Query("SELECT * FROM appointment entity WHERE entity.adviser_id IS NULL")
    Flux<Appointment> findAllWhereAdviserIsNull();

    @Query("SELECT * FROM appointment entity WHERE entity.manager_id = :id")
    Flux<Appointment> findByManager(Long id);

    @Query("SELECT * FROM appointment entity WHERE entity.manager_id IS NULL")
    Flux<Appointment> findAllWhereManagerIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Appointment> findAll();

    @Override
    Mono<Appointment> findById(Long id);

    @Override
    <S extends Appointment> Mono<S> save(S entity);
}

interface AppointmentRepositoryInternal {
    <S extends Appointment> Mono<S> insert(S entity);
    <S extends Appointment> Mono<S> save(S entity);
    Mono<Integer> update(Appointment entity);

    Flux<Appointment> findAll();
    Mono<Appointment> findById(Long id);
    Flux<Appointment> findAllBy(Pageable pageable);
    Flux<Appointment> findAllBy(Pageable pageable, Criteria criteria);
}

package com.reactnatjhip.developer.repository;

import com.reactnatjhip.developer.domain.Adviser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Adviser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdviserRepository extends R2dbcRepository<Adviser, Long>, AdviserRepositoryInternal {
    Flux<Adviser> findAllBy(Pageable pageable);

    @Query("SELECT * FROM adviser entity WHERE entity.user_id = :id")
    Flux<Adviser> findByUser(Long id);

    @Query("SELECT * FROM adviser entity WHERE entity.user_id IS NULL")
    Flux<Adviser> findAllWhereUserIsNull();

    @Query("SELECT * FROM adviser entity WHERE entity.bank_id = :id")
    Flux<Adviser> findByBank(Long id);

    @Query("SELECT * FROM adviser entity WHERE entity.bank_id IS NULL")
    Flux<Adviser> findAllWhereBankIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Adviser> findAll();

    @Override
    Mono<Adviser> findById(Long id);

    @Override
    <S extends Adviser> Mono<S> save(S entity);
}

interface AdviserRepositoryInternal {
    <S extends Adviser> Mono<S> insert(S entity);
    <S extends Adviser> Mono<S> save(S entity);
    Mono<Integer> update(Adviser entity);

    Flux<Adviser> findAll();
    Mono<Adviser> findById(Long id);
    Flux<Adviser> findAllBy(Pageable pageable);
    Flux<Adviser> findAllBy(Pageable pageable, Criteria criteria);
}

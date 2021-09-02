package com.reactnatjhip.developer.repository;

import com.reactnatjhip.developer.domain.Bank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Bank entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BankRepository extends R2dbcRepository<Bank, Long>, BankRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<Bank> findAll();

    @Override
    Mono<Bank> findById(Long id);

    @Override
    <S extends Bank> Mono<S> save(S entity);
}

interface BankRepositoryInternal {
    <S extends Bank> Mono<S> insert(S entity);
    <S extends Bank> Mono<S> save(S entity);
    Mono<Integer> update(Bank entity);

    Flux<Bank> findAll();
    Mono<Bank> findById(Long id);
    Flux<Bank> findAllBy(Pageable pageable);
    Flux<Bank> findAllBy(Pageable pageable, Criteria criteria);
}

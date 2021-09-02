package com.reactnatjhip.developer.repository;

import com.reactnatjhip.developer.domain.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Company entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyRepository extends R2dbcRepository<Company, Long>, CompanyRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<Company> findAll();

    @Override
    Mono<Company> findById(Long id);

    @Override
    <S extends Company> Mono<S> save(S entity);
}

interface CompanyRepositoryInternal {
    <S extends Company> Mono<S> insert(S entity);
    <S extends Company> Mono<S> save(S entity);
    Mono<Integer> update(Company entity);

    Flux<Company> findAll();
    Mono<Company> findById(Long id);
    Flux<Company> findAllBy(Pageable pageable);
    Flux<Company> findAllBy(Pageable pageable, Criteria criteria);
}

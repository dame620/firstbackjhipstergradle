package com.reactnatjhip.developer.repository;

import com.reactnatjhip.developer.domain.Manager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Manager entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ManagerRepository extends R2dbcRepository<Manager, Long>, ManagerRepositoryInternal {
    Flux<Manager> findAllBy(Pageable pageable);

    @Query("SELECT * FROM manager entity WHERE entity.user_id = :id")
    Flux<Manager> findByUser(Long id);

    @Query("SELECT * FROM manager entity WHERE entity.user_id IS NULL")
    Flux<Manager> findAllWhereUserIsNull();

    @Query("SELECT * FROM manager entity WHERE entity.company_id = :id")
    Flux<Manager> findByCompany(Long id);

    @Query("SELECT * FROM manager entity WHERE entity.company_id IS NULL")
    Flux<Manager> findAllWhereCompanyIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Manager> findAll();

    @Override
    Mono<Manager> findById(Long id);

    @Override
    <S extends Manager> Mono<S> save(S entity);
}

interface ManagerRepositoryInternal {
    <S extends Manager> Mono<S> insert(S entity);
    <S extends Manager> Mono<S> save(S entity);
    Mono<Integer> update(Manager entity);

    Flux<Manager> findAll();
    Mono<Manager> findById(Long id);
    Flux<Manager> findAllBy(Pageable pageable);
    Flux<Manager> findAllBy(Pageable pageable, Criteria criteria);
}

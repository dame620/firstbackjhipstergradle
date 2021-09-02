package com.reactnatjhip.developer.service;

import com.reactnatjhip.developer.domain.Company;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Company}.
 */
public interface CompanyService {
    /**
     * Save a company.
     *
     * @param company the entity to save.
     * @return the persisted entity.
     */
    Mono<Company> save(Company company);

    /**
     * Partially updates a company.
     *
     * @param company the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Company> partialUpdate(Company company);

    /**
     * Get all the companies.
     *
     * @return the list of entities.
     */
    Flux<Company> findAll();

    /**
     * Returns the number of companies available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" company.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Company> findOne(Long id);

    /**
     * Delete the "id" company.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}

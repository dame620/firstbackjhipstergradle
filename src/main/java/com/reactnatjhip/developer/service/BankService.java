package com.reactnatjhip.developer.service;

import com.reactnatjhip.developer.domain.Bank;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Bank}.
 */
public interface BankService {
    /**
     * Save a bank.
     *
     * @param bank the entity to save.
     * @return the persisted entity.
     */
    Mono<Bank> save(Bank bank);

    /**
     * Partially updates a bank.
     *
     * @param bank the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Bank> partialUpdate(Bank bank);

    /**
     * Get all the banks.
     *
     * @return the list of entities.
     */
    Flux<Bank> findAll();

    /**
     * Returns the number of banks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" bank.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Bank> findOne(Long id);

    /**
     * Delete the "id" bank.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}

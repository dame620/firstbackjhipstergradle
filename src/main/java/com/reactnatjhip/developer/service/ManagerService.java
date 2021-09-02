package com.reactnatjhip.developer.service;

import com.reactnatjhip.developer.domain.Manager;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Manager}.
 */
public interface ManagerService {
    /**
     * Save a manager.
     *
     * @param manager the entity to save.
     * @return the persisted entity.
     */
    Mono<Manager> save(Manager manager);

    /**
     * Partially updates a manager.
     *
     * @param manager the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Manager> partialUpdate(Manager manager);

    /**
     * Get all the managers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Manager> findAll(Pageable pageable);

    /**
     * Returns the number of managers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" manager.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Manager> findOne(Long id);

    /**
     * Delete the "id" manager.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}

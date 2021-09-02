package com.reactnatjhip.developer.service;

import com.reactnatjhip.developer.domain.Adviser;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Adviser}.
 */
public interface AdviserService {
    /**
     * Save a adviser.
     *
     * @param adviser the entity to save.
     * @return the persisted entity.
     */
    Mono<Adviser> save(Adviser adviser);

    /**
     * Partially updates a adviser.
     *
     * @param adviser the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Adviser> partialUpdate(Adviser adviser);

    /**
     * Get all the advisers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Adviser> findAll(Pageable pageable);

    /**
     * Returns the number of advisers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" adviser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Adviser> findOne(Long id);

    /**
     * Delete the "id" adviser.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}

package com.reactnatjhip.developer.service.impl;

import com.reactnatjhip.developer.domain.Adviser;
import com.reactnatjhip.developer.repository.AdviserRepository;
import com.reactnatjhip.developer.service.AdviserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Adviser}.
 */
@Service
@Transactional
public class AdviserServiceImpl implements AdviserService {

    private final Logger log = LoggerFactory.getLogger(AdviserServiceImpl.class);

    private final AdviserRepository adviserRepository;

    public AdviserServiceImpl(AdviserRepository adviserRepository) {
        this.adviserRepository = adviserRepository;
    }

    @Override
    public Mono<Adviser> save(Adviser adviser) {
        log.debug("Request to save Adviser : {}", adviser);
        return adviserRepository.save(adviser);
    }

    @Override
    public Mono<Adviser> partialUpdate(Adviser adviser) {
        log.debug("Request to partially update Adviser : {}", adviser);

        return adviserRepository
            .findById(adviser.getId())
            .map(
                existingAdviser -> {
                    if (adviser.getRegistrationNumber() != null) {
                        existingAdviser.setRegistrationNumber(adviser.getRegistrationNumber());
                    }
                    if (adviser.getCompany() != null) {
                        existingAdviser.setCompany(adviser.getCompany());
                    }
                    if (adviser.getDepartment() != null) {
                        existingAdviser.setDepartment(adviser.getDepartment());
                    }

                    return existingAdviser;
                }
            )
            .flatMap(adviserRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Adviser> findAll(Pageable pageable) {
        log.debug("Request to get all Advisers");
        return adviserRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return adviserRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Adviser> findOne(Long id) {
        log.debug("Request to get Adviser : {}", id);
        return adviserRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Adviser : {}", id);
        return adviserRepository.deleteById(id);
    }
}

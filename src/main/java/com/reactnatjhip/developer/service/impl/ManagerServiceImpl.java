package com.reactnatjhip.developer.service.impl;

import com.reactnatjhip.developer.domain.Manager;
import com.reactnatjhip.developer.repository.ManagerRepository;
import com.reactnatjhip.developer.service.ManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Manager}.
 */
@Service
@Transactional
public class ManagerServiceImpl implements ManagerService {

    private final Logger log = LoggerFactory.getLogger(ManagerServiceImpl.class);

    private final ManagerRepository managerRepository;

    public ManagerServiceImpl(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public Mono<Manager> save(Manager manager) {
        log.debug("Request to save Manager : {}", manager);
        return managerRepository.save(manager);
    }

    @Override
    public Mono<Manager> partialUpdate(Manager manager) {
        log.debug("Request to partially update Manager : {}", manager);

        return managerRepository
            .findById(manager.getId())
            .map(
                existingManager -> {
                    if (manager.getRegistrationNumber() != null) {
                        existingManager.setRegistrationNumber(manager.getRegistrationNumber());
                    }
                    if (manager.getDepartment() != null) {
                        existingManager.setDepartment(manager.getDepartment());
                    }

                    return existingManager;
                }
            )
            .flatMap(managerRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Manager> findAll(Pageable pageable) {
        log.debug("Request to get all Managers");
        return managerRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return managerRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Manager> findOne(Long id) {
        log.debug("Request to get Manager : {}", id);
        return managerRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Manager : {}", id);
        return managerRepository.deleteById(id);
    }
}

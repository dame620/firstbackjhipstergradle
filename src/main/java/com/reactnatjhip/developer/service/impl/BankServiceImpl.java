package com.reactnatjhip.developer.service.impl;

import com.reactnatjhip.developer.domain.Bank;
import com.reactnatjhip.developer.repository.BankRepository;
import com.reactnatjhip.developer.service.BankService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Bank}.
 */
@Service
@Transactional
public class BankServiceImpl implements BankService {

    private final Logger log = LoggerFactory.getLogger(BankServiceImpl.class);

    private final BankRepository bankRepository;

    public BankServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public Mono<Bank> save(Bank bank) {
        log.debug("Request to save Bank : {}", bank);
        return bankRepository.save(bank);
    }

    @Override
    public Mono<Bank> partialUpdate(Bank bank) {
        log.debug("Request to partially update Bank : {}", bank);

        return bankRepository
            .findById(bank.getId())
            .map(
                existingBank -> {
                    if (bank.getName() != null) {
                        existingBank.setName(bank.getName());
                    }
                    if (bank.getAddress() != null) {
                        existingBank.setAddress(bank.getAddress());
                    }

                    return existingBank;
                }
            )
            .flatMap(bankRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Bank> findAll() {
        log.debug("Request to get all Banks");
        return bankRepository.findAll();
    }

    public Mono<Long> countAll() {
        return bankRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Bank> findOne(Long id) {
        log.debug("Request to get Bank : {}", id);
        return bankRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Bank : {}", id);
        return bankRepository.deleteById(id);
    }
}

package com.reactnatjhip.developer.service.impl;

import com.reactnatjhip.developer.domain.Company;
import com.reactnatjhip.developer.repository.CompanyRepository;
import com.reactnatjhip.developer.service.CompanyService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Company}.
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Mono<Company> save(Company company) {
        log.debug("Request to save Company : {}", company);
        return companyRepository.save(company);
    }

    @Override
    public Mono<Company> partialUpdate(Company company) {
        log.debug("Request to partially update Company : {}", company);

        return companyRepository
            .findById(company.getId())
            .map(
                existingCompany -> {
                    if (company.getName() != null) {
                        existingCompany.setName(company.getName());
                    }
                    if (company.getNinea() != null) {
                        existingCompany.setNinea(company.getNinea());
                    }
                    if (company.getRc() != null) {
                        existingCompany.setRc(company.getRc());
                    }
                    if (company.getAddress() != null) {
                        existingCompany.setAddress(company.getAddress());
                    }

                    return existingCompany;
                }
            )
            .flatMap(companyRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Company> findAll() {
        log.debug("Request to get all Companies");
        return companyRepository.findAll();
    }

    public Mono<Long> countAll() {
        return companyRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Company> findOne(Long id) {
        log.debug("Request to get Company : {}", id);
        return companyRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Company : {}", id);
        return companyRepository.deleteById(id);
    }
}

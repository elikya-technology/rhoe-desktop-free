/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.service;

import com.elikya.apps.rhoe.desk.entity.Provider;
import com.elikya.apps.rhoe.desk.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderService {

    private ProviderRepository providerRepository;

    @Autowired
    public void setProviderRepository(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public List<Provider> getAll() {
        return providerRepository.findAll();
    }

    public Provider save(Provider provider) { return providerRepository.save(provider); }

    public Provider update(Provider provider) { return providerRepository.save(provider); }

    public void delete(Provider provider) {
        this.providerRepository.delete(provider);
    }

    public void deleteAll(List<Provider> providers) {
        this.providerRepository.deleteAll(providers);
    }
}

/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.service;

import com.elikya.apps.rhoe.desk.entity.Tax;
import com.elikya.apps.rhoe.desk.repository.TaxRepository;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class TaxService {

    private TaxRepository taxRepository;

    @Autowired
    private void setTaxRepository(TaxRepository taxRepository) {
        this.taxRepository = taxRepository;
    }

    public List<Tax> getAll() {
        List<Tax> taxes = taxRepository.findAll();
        convertCostsCurrency(taxes);
        return taxes;
    }

    public Tax getLast() {
        Tax last = taxRepository.queryLastInserted();
        convertCostsCurrency(Collections.singletonList(last));
        return last;
    }

    public Tax save(Tax tax) { return taxRepository.save(tax); }

    public Tax update(Tax tax) { return save(tax); }

    public void delete(Tax tax) { this.taxRepository.delete(tax); }

    public void deleteAll(List<Tax> taxes) { this.taxRepository.deleteAll(taxes); }

    private void convertCostsCurrency(List<Tax> taxes) {
        BigDecimal rate = getCurrentRate();
        taxes.forEach(it -> it.setConvertedCost(it.getCost().multiply(rate)));
    }

    private BigDecimal getCurrentRate() {
        if (ApplicationCurrency.advancedOptionsAreEnabled())
            return BigDecimal.valueOf(ApplicationCurrency.getActualRate());
        else return BigDecimal.ONE;
    }
}

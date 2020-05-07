/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.service;

import com.elikya.apps.rhoe.desk.entity.Sale;
import com.elikya.apps.rhoe.desk.repository.SaleRepository;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

    private SaleRepository saleRepository;
    private SaleLineService saleLineService;

    @Autowired
    private void setSaleRepository(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Autowired
    private void setSaleLineService(SaleLineService saleLineService) {
        this.saleLineService = saleLineService;
    }

    public List<Sale> getAll() {
        List<Sale> sales = saleRepository.findAll();
        convertTotalPrices(sales);
        includeLines(sales);
        return sales;
    }

    public Optional<Sale> getFromId(int id) {
        Optional<Sale> sale = saleRepository.findById(id);
        sale.ifPresent(it -> includeLines(Collections.singletonList(it)));
        return sale;
    }

    public List<Sale> getFromPeriod(LocalDate from, LocalDate to) {
        List<Sale> sales = saleRepository.getByPeriod(from, to);
        convertTotalPrices(sales);
        includeLines(sales);
        return sales;
    }

    private void convertTotalPrices(List<Sale> sales) {
        sales.forEach(it -> {
            if (ApplicationCurrency.advancedOptionsAreEnabled()) {
                    multiplyPrices(it);
            } else {
                divideMoneyReceived(it);
            }
        });
    }

    private void divideMoneyReceived(Sale it) {
        if (!it.getCurrency().equals(ApplicationCurrency.getDefaultCurrency())) {
            double currencyRate = getConversionRate(it);
            BigDecimal convertedMoneyReceived = it.getMoneyReceived()
                    .divide(BigDecimal.valueOf(currencyRate), 3);
            it.setMoneyReceived(convertedMoneyReceived);
        }
    }

    private void multiplyPrices(Sale it) {
        double rate = getConversionRate(it);
        BigDecimal convertedPrice = it.getTotalPrice().multiply(BigDecimal.valueOf(rate));
        it.setTotalPrice(convertedPrice);
        BigDecimal convertedPriceTax = it.getTaxedPrice().multiply(BigDecimal.valueOf(rate));
        it.setTaxedPrice(convertedPriceTax);
        multiplyMoneyReceived(it, rate);
    }

    private void multiplyMoneyReceived(Sale it, double rate) {
        if (it.getCurrency().equals(ApplicationCurrency.getDefaultCurrency())) {
            BigDecimal convertedMoneyReceived = it.getMoneyReceived().multiply(BigDecimal.valueOf(rate));
            it.setMoneyReceived(convertedMoneyReceived);
        }
    }

    private double getConversionRate(Sale sale) {
        if (sale.getCurrency().toLowerCase()
                .equals(ApplicationCurrency.getDefaultCurrency().toLowerCase()))
            return ApplicationCurrency.getActualRate();
        return sale.getRate();
    }

    private void includeLines(List<Sale> sales) {
        sales.forEach(it -> it.setLines(saleLineService.getFromSale(it)));
    }

    public Sale save(Sale sale) {
        Sale item = saleRepository.save(sale);
        this.saleLineService.saveAll(sale.getLines());
        return item;
    }

    public Sale getLast() {
        Sale sale = saleRepository.getLastInserted();
        List<Sale> singleSale = Collections.singletonList(sale);
        convertTotalPrices(singleSale);
        includeLines(singleSale);
        return sale;
    }

    public List<Sale> getFromIds(List<Integer> ids) {
        List<Sale> sales = this.saleRepository.getFromIds(ids);
        convertTotalPrices(sales);
        includeLines(sales);
        return sales;
    }

    public Sale update(Sale sale) {
        this.saleLineService.deleteFromSales(Collections.singletonList(sale));
        return save(sale);
    }

    public void delete(Sale sale) { this.saleRepository.delete(sale); }

    public void deleteAll(List<Sale> sales) {
        this.saleLineService.deleteFromSales(sales);
        this.saleRepository.deleteAll(sales);
    }

    public void deleteFromIds(List<Integer> ids) {
        this.saleRepository.deleteFromIds(ids);
    }

}

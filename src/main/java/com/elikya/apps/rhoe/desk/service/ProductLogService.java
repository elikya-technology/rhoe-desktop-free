/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.service;

import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.ProductLog;
import com.elikya.apps.rhoe.desk.repository.ProductLogRepository;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductLogService {

    private ProductLogRepository productLogRepository;

    @Autowired
    private void setProductLogRepository(ProductLogRepository productLogRepository) {
        this.productLogRepository = productLogRepository;
    }

    public List<ProductLog> getByProduct(Product product) {
        List<ProductLog> logs = productLogRepository.queryByProduct(product.getId());
        convertUnitPrice(logs);
        computeTotalPrice(logs);
        insertHyphensOnEmptyLogs(logs);
        return logs;
    }

    private void computeTotalPrice(List<ProductLog> logs) {
        logs.forEach(it -> {
            BigDecimal totalPrice = it.getUnitPrice()
                    .multiply(BigDecimal.valueOf(it.getActionQty()));
            it.setTotalPrice(totalPrice);
        });
    }

    private void convertUnitPrice(List<ProductLog> logs) {
        if (ApplicationCurrency.advancedOptionsAreEnabled()) {
            logs.forEach(it -> {
                double rate = getConversionRate(it);
                it.setUnitPrice(it.getUnitPrice().multiply(BigDecimal.valueOf(rate)));
            });
        }
    }

    private void insertHyphensOnEmptyLogs(List<ProductLog> logs) {
        logs.forEach(it -> {
            if (it.getReason() ==  null || it.getReason().trim().isEmpty())
                it.setReason("-");
        });
    }

    public List<ProductLog> getByProduct(Product product, LocalDate from, LocalDate to) {
        List<ProductLog> logs = productLogRepository.queryByProduct(product.getId(), from, to);
        convertUnitPrice(logs);
        computeTotalPrice(logs);
        insertHyphensOnEmptyLogs(logs);
        return logs;
    }

    public List<ProductLog> getByIdsBetweenDates(List<Integer> ids, LocalDate from, LocalDate to) {
        List<ProductLog> logs = productLogRepository.findByProductsIdsBetweenDates(ids, from, to);
        convertUnitPrice(logs);
        computeTotalPrice(logs);
        insertHyphensOnEmptyLogs(logs);
        return logs;
    }

    public List<ProductLog> getAllByIds(List<Integer> ids) {
        List<ProductLog> logs = productLogRepository.findByProductsIds(ids);
        convertUnitPrice(logs);
        computeTotalPrice(logs);
        insertHyphensOnEmptyLogs(logs);
        return logs;
    }

    private double getConversionRate(ProductLog log) {
        if (log.getActualCurrency().toLowerCase()
                .equals(ApplicationCurrency.getDefaultCurrency().toLowerCase()))
            return ApplicationCurrency.getActualRate();
        return log.getCurrencyRate();
    }

    public ProductLog save(ProductLog productLog) { return productLogRepository.save(productLog); }

    public void saveAll(List<ProductLog> logs) {
        this.productLogRepository.saveAll(logs);
    }

    public ProductLog update(ProductLog productLog) { return save(productLog); }

    public void delete(ProductLog productLog) { this.productLogRepository.delete(productLog); }

    public void deleteAll() { this.productLogRepository.deleteAll(); }

    public void deleteAll(List<ProductLog> productLogs) { this.productLogRepository.deleteAll(productLogs); }

    public void deleteFromProducts(List<Product> products) {
        List<Integer> ids = products.stream().map(Product::getId).collect(Collectors.toList());
        this.productLogRepository.deleteByProductsIds(ids);
    }

}

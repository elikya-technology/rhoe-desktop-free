/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.service;

import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.Tax;
import com.elikya.apps.rhoe.desk.repository.ProductRepository;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private ProductLogService productLogService;
    private TaxService taxService;

    @Autowired
    private void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    private void setProductLogService(ProductLogService productLogService) {
        this.productLogService = productLogService;
    }

    @Autowired
    private void setTaxService(TaxService taxService) {
        this.taxService = taxService;
    }

    public List<Product> getAll() {
        List<Product> products = productRepository.findAll();
        includeTax(products);
        convertUnitPrice(products);
        includeStockPrices(products);
        return products;
    }

    public List<Product> getFromCategory(int id) {
        List<Product> products = productRepository.getFromCategory(id);
        includeTax(products);
        convertUnitPrice(products);
        includeStockPrices(products);
        return products;
    }

    public List<Product> getFromProvider(int id) {
        List<Product> products = productRepository.getFromProvider(id);
        includeTax(products);
        convertUnitPrice(products);
        includeStockPrices(products);
        return products;
    }

    public Product getLast() {
        Product product = productRepository.getLastInserted();
        List<Product> singleProduct = Collections.singletonList(product);
        includeTax(singleProduct);
        convertUnitPrice(singleProduct);
        includeStockPrices(singleProduct);
        return product;
    }

    public Optional<Product> getFromId(int id) {
        Optional<Product> product = productRepository.findById(id);
        product.map(Collections::singletonList).ifPresent(it -> {
            includeTax(it);
            convertUnitPrice(it);
        });
        return product;
    }

    public void convertUnitPrice(List<Product> products) {
        if (ApplicationCurrency.advancedOptionsAreEnabled()) {
            BigDecimal conversionValue = BigDecimal.valueOf(ApplicationCurrency.getActualRate());
            products.forEach(it -> setConvertedPrices(conversionValue, it));
        } else {
            products.forEach(this::keepUnconvertedPrices);
        }
    }

    private void keepUnconvertedPrices(Product it) {
        it.setConvertedUnitPrice(it.getUnitPrice());
        it.setConvertedUnitPriceTax(it.getUnitPriceTax());
    }

    private void setConvertedPrices(BigDecimal conversionValue, Product it) {
        it.setConvertedUnitPrice((it.getUnitPrice().multiply(conversionValue)));
        it.setConvertedUnitPriceTax(it.getUnitPriceTax().multiply(conversionValue));
    }

    private void includeStockPrices(List<Product> products) {
        includeStockPrice(products);
        includeStockPriceTax(products);
    }

    private void includeStockPrice(List<Product> products) {
        products.forEach(it -> it.setStockPrice(it.getConvertedUnitPrice()
                .multiply(BigDecimal.valueOf(it.getStockQuantity()))));
    }

    private void includeStockPriceTax(List<Product> products) {
        products.forEach(it -> it.setStockPriceTax(it.getConvertedUnitPriceTax()
                .multiply(BigDecimal.valueOf(it.getStockQuantity()))));
    }

    public void includeTax(List<Product> products) {
        List<Tax> taxes = taxService.getAll();
        products.forEach(it -> it.setUnitPriceTax(computeTax(it.getUnitPrice(), taxes)));
    }

    private BigDecimal computeTax(BigDecimal value, List<Tax> taxes) {
        BigDecimal result = value;
        for (Tax tax : taxes) {
            if (tax.getCost().doubleValue() > 0)
                result = result.add(tax.getCost());
            else {
                BigDecimal percent = value.multiply(tax.getPercent())
                        .divide(BigDecimal.valueOf(100), 3);
                result = result.add(percent);
            }
        }
        return result;
    }

    public Product save(Product product) throws DataIntegrityViolationException {
        return productRepository.save(product);
    }

    public void updateProductsQty(List<Product> products) {
        Map<Integer, Integer> items = mapQuantitiesIds(products);
        items.forEach((id, qty) -> this.productRepository.updateProductQty(id, qty));
    }

    private Map<Integer, Integer> mapQuantitiesIds(List<Product> products) {
        Map<Integer, Integer> quantitiesIds = new HashMap<>();
        products.forEach(it -> quantitiesIds.put(it.getId(), it.getStockQuantity()));
        return quantitiesIds;
    }

    public Product update(Product product) {
        return save(product);
    }

    public void delete(Product product) {
        this.productRepository.delete(product);
    }

    public void deleteAll(List<Product> products) {
        this.productLogService.deleteFromProducts(products);
        this.productRepository.deleteAll(products);
    }

}

/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.service;

import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.repository.ProductRepository;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private ProductLogService productLogService;

    @Autowired
    private void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    private void setProductLogService(ProductLogService productLogService) {
        this.productLogService = productLogService;
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
        final BigDecimal vat = new BigDecimal(RhoeConfig.get().getProperty("vat"));

        products.forEach(it -> {
            BigDecimal unitPrice = it.getUnitPrice();
            BigDecimal taxAmount = unitPrice.multiply(vat).divide(BigDecimal.valueOf(100), 3);
            it.setUnitPriceTax(unitPrice.add(taxAmount));
        });
    }

    public Product save(Product product) {
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

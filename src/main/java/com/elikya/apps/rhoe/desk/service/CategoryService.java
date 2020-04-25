/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.service;

import com.elikya.apps.rhoe.desk.entity.Category;
import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;
    private ProductService productService;

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    private void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public List<Category> getAll() {
        List<Category> categories = categoryRepository.findAll();
        categories.forEach(it -> {
            List<Product> products = productService.getFromCategory(it.getId());
            it.setProductsNumber(computeProductsNumber(products));
            it.setProductsQty(computeProductsQty(products));
            it.setProductsStockPrice(computeProductPriceSum(products));
            it.setProducts(products);
        });
        return categories;
    }

    public int computeProductsNumber(List<Product> products) {
        return products.size();
    }

    public int computeProductsQty(List<Product> products) {
        return products.stream().mapToInt(Product::getStockQuantity).sum();
    }

    public BigDecimal computeProductPriceSum(List<Product> products) {
        return products.stream().map(it -> it.getConvertedUnitPrice()
                .multiply(BigDecimal.valueOf(it.getStockQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Category update(Category category) {
        return categoryRepository.save(category);
    }

    public void delete(Category category) {
        this.categoryRepository.delete(category);
    }

    public void deleteAll(List<Category> categories) {
        this.categoryRepository.deleteAll(categories);
    }

}

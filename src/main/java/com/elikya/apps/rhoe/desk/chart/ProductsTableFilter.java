/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.chart;

import com.elikya.apps.rhoe.desk.entity.Product;
import static java.util.Comparator.comparing;
import java.util.List;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author Mafole Loemelah
 */
public class ProductsTableFilter {

    public static List<Product> groupByStockQuantity(List<Product> products) {
        return products.stream().collect(groupingBy(Product::getStockQuantity))
                .values().stream().flatMap(list -> list.stream()).collect(toList())
                .stream().sorted(comparing(product -> product.getStockQuantity())).collect(toList());
    }

    public static List<Product> groupByPrice(List<Product> products) {
        return products.stream().collect(groupingBy(Product::getUnitPrice))
                .values().stream().flatMap(list -> list.stream()).collect(toList()).stream()
                .sorted(comparing(product -> product.getUnitPrice())).collect(toList());
    }

    public static List<Product> groupByName(List<Product> products) {
        return products.stream().collect(groupingBy(Product::getLabel))
                .values().stream().flatMap(list -> list.stream()).collect(toList()).stream()
                .sorted(comparing(product -> product.getLabel().toLowerCase()))
                .collect(toList());
    }

    public static List<Product> groupById(List<Product> products) {
        return products.stream()
                .collect(groupingBy(Product::getId)).values().stream()
                .flatMap(list -> list.stream()).collect(toList()).stream()
                .sorted(comparing(product -> product.getId())).collect(toList());
    }

    public static List<Product> groupByCategory(List<Product> products) {
        return products.stream()
                .collect(groupingBy(Product::getCategory)).values().stream()
                .flatMap(list -> list.stream()).collect(toList()).stream()
                .sorted(comparing(product -> product.getCategory().getLabel()
                .toLowerCase())).collect(toList());
    }
}

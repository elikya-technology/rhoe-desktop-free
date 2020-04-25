/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.chart;

import com.elikya.apps.rhoe.desk.entity.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 *
 * @author Mafole Loemelah
 */
public class ProductsDataGrouper {

    public static ObservableList<Series<String, Number>> productsNumberPerCategory(List<Product> products) {
        ObservableList<Series<String, Number>> series = FXCollections.observableArrayList();
        products.stream().collect(groupingBy(Product::getCategory, counting())).forEach((key, value) -> {
            Series<String, Number> serie = new Series<>();
            serie.setName(key.getLabel());
            serie.getData().add(new Data<>("", value, key.getId()));
            series.add(serie);
        });
        return series;
    }
    
    public static ObservableList<Series<String, Number>> productsQuantities(List<Product> products) {
        ObservableList<Series<String, Number>> series = FXCollections.observableArrayList();
        products.forEach(p -> {
            Series<String, Number> serie = new Series<>();
            serie.setName(p.getNumber());
            serie.getData().add(new Data<>("", p.getStockQuantity(), p.getId()));
            series.add(serie);
        });
        return series;
    }
    
    public static ObservableList<Series<String, Number>> categoriesProductsPrices(List<Product> products) {
        ObservableList<Series<String, Number>> series = FXCollections.observableArrayList();
        products.stream().collect(groupingBy(Product::getCategory)).forEach((key, value) -> {
            BigDecimal price = value.stream().map(Product::getStockPriceTax).reduce(BigDecimal.ZERO, BigDecimal::add);
            Series<String, Number> serie = new Series<>();
            serie.setName(key.getLabel());
            serie.getData().add(new Data<>("", price, key.getId()));
            series.add(serie);
        });
        return series;
    }
    
    public static ObservableList<Series<String, Number>> productsPrices(List<Product> products) {
        ObservableList<Series<String, Number>> series = FXCollections.observableArrayList();
        products.forEach(p -> {
            Series<String, Number> serie = new Series<>();
            serie.setName(p.getNumber());
            serie.getData().add(new Data<>("", p.getStockPriceTax(), p.getId()));
            series.add(serie);
        });
        return series;
    }

}

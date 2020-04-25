/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.chart;

import com.elikya.apps.rhoe.desk.entity.Product;
import javafx.scene.chart.BarChart;

import java.util.List;

import static com.elikya.apps.rhoe.desk.chart.ProductsDataGrouper.*;

/**
 *
 * @author Mafole Loemelah
 */
public class ProductsCharter {

    public static BarChart<String, Number> chartProducts(ChartContext target, List<Product> list) {
        switch (target) {
            case CATEGORY_QUANTITY: return ChartsUtils.buildChart(productsNumberPerCategory(list), ChartsUtils.ComputeContext.QUANTITY);
            case CATEGORY_PRICE: return ChartsUtils.buildChart(categoriesProductsPrices(list), ChartsUtils.ComputeContext.PRICE);
            case PRODUCTS_PRICE: return ChartsUtils.buildChart(productsPrices(list), ChartsUtils.ComputeContext.PRICE);
            default: return ChartsUtils.buildChart(productsQuantities(list), ChartsUtils.ComputeContext.QUANTITY);
        }
    }
    
    public enum ChartContext {CATEGORY_QUANTITY, PRODUCTS_QUANTITIES, CATEGORY_PRICE, PRODUCTS_PRICE}
}

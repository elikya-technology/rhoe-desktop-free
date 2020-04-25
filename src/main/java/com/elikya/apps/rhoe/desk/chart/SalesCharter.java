/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.chart;

import com.elikya.apps.rhoe.desk.chart.ChartsUtils.ComputeContext;
import com.elikya.apps.rhoe.desk.entity.Sale;
import javafx.scene.chart.BarChart;

import java.util.List;

import static com.elikya.apps.rhoe.desk.chart.SalesDataGrouper.*;

/**
 *
 * @author Mafole Loemelah
 */
public class SalesCharter {

    public static BarChart<String, Number> chartSales(ChartContext context, List<Sale> sales, ComputeContext computeContext) {
        switch (context) {
            case DAY_OF_MONTH: return ChartsUtils.buildChart(salesPerDayOfMonth(sales, computeContext), computeContext);
            case DAY_OF_WEEK: return ChartsUtils.buildChart(salesPerDayOfWeek(sales, computeContext), computeContext);
            case MONTH: return ChartsUtils.buildChart(salesPerMonth(sales, computeContext), computeContext);
            case YEAR: return ChartsUtils.buildChart(salesPerYear(sales, computeContext), computeContext);
            default: return ChartsUtils.buildChart(products(sales, computeContext), computeContext);
        }
    }
    
    public enum ChartContext {MONTH, DAY_OF_MONTH, DAY_OF_WEEK, YEAR, PRODUCTS}
}

/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.chart;

import com.elikya.apps.rhoe.desk.chart.ChartsUtils.ComputeContext;
import com.elikya.apps.rhoe.desk.entity.SaleLine;
import javafx.scene.chart.BarChart;

import java.util.List;

import static com.elikya.apps.rhoe.desk.chart.SaleLineDataGrouper.*;

/**
 *
 * @author Mafole Loemelah
 */
public class SaleLineCharter {

    public static BarChart<String, Number> chart(List<SaleLine> ligns
            , ChartContext chartContext, ComputeContext computeContext) {
        switch (chartContext) {
            case MONTH: return ChartsUtils.buildChart(collectByMonth(ligns, computeContext), computeContext);
            case DAY_OF_MONTH: return ChartsUtils.buildChart(collectByDayOfMonth(ligns, computeContext), computeContext);
            case DAY_OF_WEEK: return ChartsUtils.buildChart(collectByDayOfWeek(ligns, computeContext), computeContext);
            case YEAR: return ChartsUtils.buildChart(collectByYear(ligns, computeContext), computeContext);
            default: return ChartsUtils.buildChart(collectByProducts(ligns, computeContext), computeContext);
        }
    }
    
    public enum ChartContext {MONTH, DAY_OF_MONTH, DAY_OF_WEEK, YEAR, PRODUCTS}
}

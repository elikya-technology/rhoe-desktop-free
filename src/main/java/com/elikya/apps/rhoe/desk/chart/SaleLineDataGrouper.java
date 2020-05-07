/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.chart;

import com.elikya.apps.rhoe.desk.chart.ChartsUtils.ComputeContext;
import com.elikya.apps.rhoe.desk.entity.SaleLine;
import com.elikya.apps.rhoe.desk.ui.PeriodsNames;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableArrayList;

/**
 *
 * @author Mafole Loemelah
 */
public class SaleLineDataGrouper {

    public static ObservableList<Series<String, Number>> collectByMonth(
            List<SaleLine> ligns, ComputeContext context) {
        ObservableList<Series<String, Number>> series = observableArrayList();
        ligns.stream().collect(Collectors.groupingBy(l -> l.getSale()
                .getSaleDate().getMonth().getValue())).forEach((key, value) -> {
            String name = PeriodsNames.getMonthName(key);
            BigDecimal sum = context.equals(ComputeContext.PRICE)
                    ? computePrice(value) : computeQty(value);
            Series<String, Number> serie = buildSerie(name, sum, key);
            series.add(serie);
        });
        return series;
    }

    public static ObservableList<Series<String, Number>> collectByDayOfMonth(
            List<SaleLine> ligns, ComputeContext context) {
        ObservableList<Series<String, Number>> series = observableArrayList();
        ligns.stream().collect(Collectors.groupingBy(l -> l.getSale()
                .getSaleDate().getDayOfMonth())).forEach((key, value) -> {
            String name = String.valueOf(key);
            BigDecimal sum = context.equals(ComputeContext.PRICE)
                    ? computePrice(value) : computeQty(value);
            Series<String, Number> serie = buildSerie(name, sum, key);
            series.add(serie);
        });
        return series;
    }

    public static ObservableList<Series<String, Number>> collectByDayOfWeek(
            List<SaleLine> ligns, ComputeContext context) {
        ObservableList<Series<String, Number>> series = observableArrayList();
        ligns.stream().collect(Collectors.groupingBy(l -> l.getSale()
                .getSaleDate().getDayOfWeek().getValue())).forEach((key, value) -> {
            String name = PeriodsNames.getWeekDayName(key);
            BigDecimal sum = context.equals(ComputeContext.PRICE)
                    ? computePrice(value) : computeQty(value);
            Series<String, Number> serie = buildSerie(name, sum, key);
            series.add(serie);
        });
        return series;
    }

    public static ObservableList<Series<String, Number>> collectByYear(
            List<SaleLine> ligns, ComputeContext context) {
        ObservableList<Series<String, Number>> series = observableArrayList();
        ligns.stream().collect(Collectors.groupingBy(l -> l.getSale()
                .getSaleDate().getYear())).forEach((key, value) -> {
            String name = String.valueOf(key);
            BigDecimal sum = context.equals(ComputeContext.PRICE)
                    ? computePrice(value) : computeQty(value);
            Series<String, Number> serie = buildSerie(name, sum, key);
            series.add(serie);
        });
        return series;
    }
    
    public static ObservableList<Series<String, Number>> collectByProducts(
            List<SaleLine> ligns, ComputeContext context) {
        ObservableList<Series<String, Number>> series = observableArrayList();
        ligns.stream().collect(Collectors.groupingBy(SaleLine::getProduct)).forEach((key, value) -> {
            BigDecimal sum = context.equals(ComputeContext.PRICE)
                    ? computePrice(value) : computeQty(value);
            Series<String, Number> serie = buildSerie(key.getNumber(), sum, key.getId());
            series.add(serie);
        });
        return series;
    }

    private static BigDecimal computePrice(List<SaleLine> ligns) {
        return ligns.stream().map(SaleLine::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal computeQty(List<SaleLine> ligns) {
        return ligns.stream().map(it -> BigDecimal.valueOf(it.getQuantity())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static XYChart.Series<String, Number> buildSerie(String name, BigDecimal sum, int extra) {
        Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(name);
        serie.getData().add(new XYChart.Data<>("", sum, extra));
        return serie;
    }
    
}

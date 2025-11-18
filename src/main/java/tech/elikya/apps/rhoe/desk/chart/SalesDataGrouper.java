/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.chart;

import tech.elikya.apps.rhoe.desk.chart.ChartsUtils.ComputeContext;
import tech.elikya.apps.rhoe.desk.entity.Sale;
import tech.elikya.apps.rhoe.desk.entity.SaleLine;
import tech.elikya.apps.rhoe.desk.ui.PeriodsNames;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 *
 * @author Mafole Loemelah
 */
public class SalesDataGrouper {

    public static ObservableList<Series<String, Number>> salesPerMonth(List<Sale> sales, ComputeContext context) {
        ObservableList<Series<String, Number>> series = FXCollections.observableArrayList();
        sales.stream().collect(groupingBy(s -> s.getSaleDate().getMonth().getValue())).forEach((key, value) -> {
            String name = PeriodsNames.getMonthName(key);
            boolean isPrice = context.equals(ComputeContext.PRICE); 
            Number sum = isPrice ? computePrice(value) : computeProductsNumber(value);
            Series<String, Number> serie = buildSerie(name, sum, key);
            series.add(serie);
        });
        return series;
    }

    public static ObservableList<Series<String, Number>> salesPerDayOfWeek(List<Sale> sales, ComputeContext context) {
        ObservableList<Series<String, Number>> series = FXCollections.observableArrayList();
        sales.stream().collect(groupingBy(s -> s.getSaleDate().getDayOfWeek().getValue())).forEach((key, value) -> {
            String name = PeriodsNames.getWeekDayName(key);
            boolean isPrice = context.equals(ComputeContext.PRICE); 
            Number sum = isPrice ? computePrice(value) : computeProductsNumber(value);
            Series<String, Number> serie = buildSerie(name, sum, key);
            series.add(serie);
        });
        return series;
    }

    public static ObservableList<Series<String, Number>> salesPerDayOfMonth(List<Sale> sales, ComputeContext context) {
        ObservableList<Series<String, Number>> series = FXCollections.observableArrayList();
        sales.stream().collect(groupingBy(s -> s.getSaleDate().getDayOfMonth()))
                .forEach(processSalesBiConsumer(context, series));
        return series;
    }

    public static ObservableList<Series<String, Number>> salesPerYear(List<Sale> sales, ComputeContext context) {
        ObservableList<Series<String, Number>> series = FXCollections.observableArrayList();
        sales.stream().collect(groupingBy(s -> s.getSaleDate().getYear()))
                .forEach(processSalesBiConsumer(context, series));
        return series;
    }

    private static BiConsumer<Integer, List<Sale>> processSalesBiConsumer(
            ComputeContext context, ObservableList<Series<String, Number>> series) {
        return (key, value) -> {
            String name = String.valueOf(key);
            boolean isPrice = context.equals(ComputeContext.PRICE);
            Number sum = isPrice ? computePrice(value) : computeProductsNumber(value);
            Series<String, Number> serie = buildSerie(name, sum, key);
            series.add(serie);
        };
    }

    public static ObservableList<Series<String, Number>> products(List<Sale> sales, ComputeContext context) {
        ObservableList<Series<String, Number>> series = FXCollections.observableArrayList();
        sales.stream().flatMap(s -> s.getLines().stream()).collect(groupingBy(SaleLine::getProduct))
                .forEach((key, value) -> {
                    boolean isPrice = context.equals(ComputeContext.PRICE);
                    Number sum = isPrice ? computeProductsPrices(value) : computeProductsQuantity(value);
                    String name = key.getNumber();
                    Series<String, Number> serie = buildSerie(name, sum, key.getId());
                    series.add(serie);
        });
        return series;
    }

    private static Series<String, Number> buildSerie(String name, Number sum, int extra) {
        Series<String, Number> serie = new Series<>();
        serie.setName(name);
        serie.getData().add(new XYChart.Data<>("", sum, extra));
        return serie;
    }
    
    private static BigDecimal computePrice(List<Sale> value) {
        return value.stream().map(Sale::getTaxedPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private static int computeProductsNumber(List<Sale> value) {
        return value.stream().flatMap((it -> it.getLines().stream()))
                .map(SaleLine::getProduct).collect(Collectors.toSet()).size();
    }
    
    private static int computeProductsQuantity(List<SaleLine> value) {
        return value.stream().mapToInt(SaleLine::getQuantity).sum();
    }
    
    private static BigDecimal computeProductsPrices(List<SaleLine> value) {
        return value.stream().map(SaleLine::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
}

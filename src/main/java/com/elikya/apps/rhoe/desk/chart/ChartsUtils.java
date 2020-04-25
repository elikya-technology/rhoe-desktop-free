/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.chart;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;

import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import static com.elikya.apps.rhoe.desk.util.NumbersFormatter.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mafole Loemelah
 */
public class ChartsUtils {

    public static final int PREF_HEIGHT = 300;

    public static BigDecimal getUpperBound(ObservableList<XYChart.Series<String, Number>> series) {
        Optional<Number> upperBound = series.stream().flatMap((XYChart.Series<String, Number> serie) 
                -> serie.getData().stream().map(XYChart.Data::getYValue))
                .max(Comparator.comparing(Number::doubleValue));
        return upperBound.map(it -> BigDecimal.valueOf(it.doubleValue())).orElse(BigDecimal.ZERO);
    }

    public static void setTooltip(BarChart<String, Number> chart, ComputeContext context) {
        chart.getData().forEach(data -> data.getData().forEach(item -> {
            Node sliceNode = item.getNode();
            String message = data.getName() + " : " + (context.equals(ComputeContext.PRICE)
                    ? getFormattedString(BigDecimal.valueOf(item.getYValue().doubleValue()))
                    : item.getYValue().intValue());
            Tooltip tooltip = ControlsHandler.createTooltip("#263238", message);
            Tooltip.install(sliceNode, tooltip);
        }));
    }

    public static BarChart<String, Number> buildChart(ObservableList<XYChart.Series<String, Number>> series
            , ComputeContext context) {
        NumberAxis y = new NumberAxis();
        y.setAutoRanging(false);
        y.setLowerBound(0);
        BigDecimal upperBound = getUpperBound(series);
        y.setUpperBound(Double.parseDouble(removeThousandsSeparator(getFormattedString(upperBound))));
        if (upperBound.doubleValue() > 0) {
            int tickUnit = upperBound.intValue() / series.size();
            if (!Double.isNaN(tickUnit)) y.setTickUnit(tickUnit);
        }
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), y);
        chart.setAnimated(true);
        chart.setData(series);
        chart.setPrefHeight(PREF_HEIGHT);
        VBox.setVgrow(chart, Priority.ALWAYS);
        ChartsUtils.setTooltip(chart, context);
        return chart;
    }
    
    public enum ComputeContext {PRICE, QUANTITY}

}

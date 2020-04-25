/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.chart.ChartsUtils.ComputeContext;
import com.elikya.apps.rhoe.desk.chart.SaleLineCharter;
import com.elikya.apps.rhoe.desk.chart.SaleLineCharter.ChartContext;
import com.elikya.apps.rhoe.desk.exporters.ProductLogExporter;
import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.ProductLog;
import com.elikya.apps.rhoe.desk.entity.SaleLine;
import com.elikya.apps.rhoe.desk.service.ProductLogService;
import com.elikya.apps.rhoe.desk.service.SaleLineService;
import com.elikya.apps.rhoe.desk.ui.*;
import com.elikya.apps.rhoe.desk.util.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class ProductStockDetailsController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private Label descriptionLabel;
    @FXML private JFXTextField name;
    @FXML private JFXTextField number;
    @FXML private JFXTextField barCode;
    @FXML private JFXTextField category;
    @FXML private JFXTextField provider;
    @FXML private Label stockLabel;
    @FXML private JFXTextField maximumQuantity;
    @FXML private JFXTextField minimumQuantity;
    @FXML private JFXTextField realQuantity;
    @FXML private JFXTextField modelNumber;
    @FXML private JFXDatePicker from;
    @FXML private JFXDatePicker to;
    @FXML private MenuItem excel;
    @FXML private CustomTextField searchText;
    @FXML private TableView<ProductLog> productLogsTable;
    @FXML private JFXButton all;
    @FXML private JFXComboBox<String> groupBy;
    @FXML private JFXTextField stockPrice;
    @FXML private TableColumn<ProductLog, LocalDate> date;
    @FXML private TableColumn<ProductLog, LocalTime> time;
    @FXML private TableColumn<ProductLog, String> action;
    @FXML private TableColumn<ProductLog, Integer> actionQty;
    @FXML private TableColumn<ProductLog, BigDecimal> actionPrice;
    @FXML private TableColumn<ProductLog, BigDecimal> totalPrice;
    @FXML private TableColumn<ProductLog, Integer> stockQty;
    @FXML private TableColumn<ProductLog, String> actionCurrency;
    @FXML private TableColumn<ProductLog, Double> currencyRate;
    @FXML private TableColumn<ProductLog, String> reason;
    @FXML private JFXComboBox<String> computeBy;
    @FXML private VBox chartBox;
    @FXML private Label chartLabel;
    @FXML private JFXButton searchBtn;
    @FXML private JFXTextField unitPriceIT;
    @FXML private JFXTextField unitPriceET;
    @FXML private SplitPane splitPane;
    @FXML private JFXTextField stockPriceT;

    private ProductLogService productLogService;
    private SaleLineService saleLineService;
    private Properties lang;
    private static Product product;
    private String currency;
    private List<SaleLine> ligns;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currency = ApplicationCurrency.getActualCurrency();
        TableViewOperation.setTableSelectionModel(productLogsTable);
        TableViewOperation.handleSelection(productLogsTable);
        ControlsHandler.handleSearchZone(searchText, searchBtn);
        SplitPaneDividerManager.resize(splitPane);
        setLanguage();
        setCellsValuesFact();
        setActionCellFactory();
        setActionPriceCellFactory();
        setTotalPriceCellFactory();
        addEventHandlers();
        fillProductInfos();
        addGroupByItems();
        addComputeByItems();
        setDatesValues();
        queryLogsFromPeriod();
        querySaleLigns(PeriodContext.PERIOD);
        setDatePickerValueProperty(from);
        setDatePickerValueProperty(to);
        setAllTooltip(PeriodContext.ALL);
        setCurrencyRateCellFactory();
    }
    
    @Autowired
    private void setProductLogService(ProductLogService productLogService) {
        this.productLogService = productLogService;
    }

    @Autowired
    private void setSaleLineService(SaleLineService saleLineService) {
        this.saleLineService = saleLineService;
    }

    public void addEventHandlers() {
        setCloseEventHandler();
        setAllEventHandler();
        setExcelEventHandler();
        setGroupByEventHandler();
        setComputeByEventHandler();
    }
    
    public static void setProduct(Product p) {product = p;}

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setLanguage() {
        lang = ControlsHandler.getLanguage();
        bundleFirst();
        bundleSecond();
    }
    
    private void setDatesValues() {
        from.setValue(DayOfWeekPicker.getFirstDayOfWeek());
        to.setValue(DayOfWeekPicker.getActualDay());
    }

    private void bundleSecond() {
        stockQty.setText(lang.getProperty("stock_quantity"));
        minimumQuantity.setPromptText(lang.getProperty("minimum_quantity"));
        realQuantity.setPromptText(lang.getProperty("stock_quantity"));
        descriptionLabel.setText(lang.getProperty("description"));
        stockLabel.setText(lang.getProperty("stock"));
        chartLabel.setText(lang.getProperty("sales"));
        modelNumber.setPromptText(lang.getProperty("serial_number"));
        from.setPromptText(lang.getProperty("from"));
        to.setPromptText(lang.getProperty("to"));
        excel.setText(lang.getProperty("export"));
        searchText.setPromptText(lang.getProperty("search"));
        String currencySymbol = " (" + currency + ")";
        actionPrice.setText(lang.getProperty("unit_price_tax") + currencySymbol);
        totalPrice.setText(lang.getProperty("total_price_tax") + currencySymbol);
        unitPriceIT.setPromptText(lang.getProperty("unit_price_tax") + currencySymbol);
        stockPrice.setPromptText(lang.getProperty("price") + currencySymbol);
        stockPriceT.setPromptText(lang.getProperty("price_tax") + currencySymbol);
        productLogsTable.setPlaceholder(new Label(lang.getProperty("table_prompt")));
        unitPriceET.setPromptText(lang.getProperty("unit_price") + currencySymbol);
    }

    private void bundleFirst() {
        title.setText(lang.getProperty("product_moves"));
        groupBy.setPromptText(lang.getProperty("group_by"));
        computeBy.setPromptText(lang.getProperty("compute_by"));
        date.setText(lang.getProperty("date"));
        time.setText(lang.getProperty("time"));
        action.setText(lang.getProperty("action"));
        actionQty.setText(lang.getProperty("quantity"));
        name.setPromptText(lang.getProperty("name"));
        number.setPromptText(lang.getProperty("product_number"));
        barCode.setPromptText(lang.getProperty("barcode"));
        category.setPromptText(lang.getProperty("category"));
        provider.setPromptText(lang.getProperty("provider"));
        maximumQuantity.setPromptText(lang.getProperty("maximum_quantity"));
        actionCurrency.setText(lang.getProperty("move_currency"));
        currencyRate.setText(lang.getProperty("move_rate"));
        reason.setText(lang.getProperty("reason"));
    }
    
    private void setCellsValuesFact() {
        date.setCellValueFactory(new PropertyValueFactory<>("logDate"));
        time.setCellValueFactory(new PropertyValueFactory<>("logTime"));
        action.setCellValueFactory(new PropertyValueFactory<>("logAction"));
        stockQty.setCellValueFactory(new PropertyValueFactory<>("stockQty"));
        actionPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        actionQty.setCellValueFactory(new PropertyValueFactory<>("actionQty"));
        actionCurrency.setCellValueFactory(new PropertyValueFactory<>("actualCurrency"));
        currencyRate.setCellValueFactory(new PropertyValueFactory<>("currencyRate"));
        reason.setCellValueFactory(new PropertyValueFactory<>("reason"));
    }
    
    private void setActionCellFactory() {
        action.setCellFactory(param -> new TableCell<ProductLog, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) setText(lang.getProperty(item));
            }
        });
    }

    private void setActionPriceCellFactory() {
        actionPrice.setCellFactory(param -> new TableCell<ProductLog, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty) setText(NumbersFormatter.getFormattedString(item));
            }
        });
    }

    private void setTotalPriceCellFactory() {
        totalPrice.setCellFactory(param -> new TableCell<ProductLog, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty) setText(NumbersFormatter.getFormattedString(item));
            }
        });

    }

    private void setCurrencyRateCellFactory() {
        currencyRate.setCellFactory(param -> new TableCell<ProductLog, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                if (!empty)
                    setText(NumbersFormatter.getFormattedString(BigDecimal.valueOf(item)));
            }
        });
    }
    
    private void fillProductInfos() {
        name.setText(product.getLabel());
        number.setText(product.getNumber());
        barCode.setText(product.getBarCode());
        category.setText(product.getCategory().toString());
        provider.setText(product.getProvider().toString());
        maximumQuantity.setText(String.valueOf(product.getMaximumQuantity()));
        minimumQuantity.setText(String.valueOf(product.getMinimumQuantity()));
        realQuantity.setText(String.valueOf(product.getStockQuantity()));
        modelNumber.setText(product.getSerialNumber());
        setPrices();
    }

    private void setPrices() {
        unitPriceET.setText(NumbersFormatter.getFormattedString(product.getConvertedUnitPrice()));
        unitPriceIT.setText(NumbersFormatter.getFormattedString(product.getConvertedUnitPriceTax()));
        stockPriceT.setText(NumbersFormatter.getFormattedString(product.getStockPriceTax()));
        stockPrice.setText(NumbersFormatter.getFormattedString(product.getStockPrice()));
    }
    
    private void addGroupByItems() {
        groupBy.getItems().addAll(FXCollections.observableArrayList(lang.getProperty("month")
                , lang.getProperty("day_month"), lang.getProperty("day_week"), lang.getProperty("year")));
        groupBy.getSelectionModel().selectFirst();
    }
    
    private void addComputeByItems() {
        computeBy.getItems().addAll(FXCollections.observableArrayList(lang
                .getProperty("price"), lang.getProperty("quantity")));
        computeBy.getSelectionModel().selectFirst();
    }
    
    private void setGroupByEventHandler() {
        groupBy.setOnAction(e -> chartSaleLigns(ligns));
    }
    
    private void setComputeByEventHandler() {
        computeBy.setOnAction(e -> chartSaleLigns(ligns));
    }
    
    private void setExcelEventHandler() {
        excel.setOnAction(e -> Platform.runLater(() -> {
            List<ProductLog> items = productLogsTable.getSelectionModel().getSelectedItems();
            if (!items.isEmpty()) {
                ProductLogExporter.export(items);
            }
        }));
    }
    
    private void setSearchFieldTextProperty(FilteredList<ProductLog> items) {
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue.trim().isEmpty()) {
               items.setPredicate(p -> true);
           } else {
               String text = newValue.toLowerCase();
               filterLogs(items, text);
               productLogsTable.refresh();
           }
        });
    }
    
    private void filterLogs(FilteredList<ProductLog> items, String text) {
        Platform.runLater(() -> items.setPredicate(it -> it.getLogDate().toString().contains(text)
                || String.valueOf(it.getActionQty()).contains(text)
                || lang.getProperty(it.getLogAction()).toLowerCase().contains(text)
                || it.getLogTime().toString().contains(text)
                || String.valueOf(it.getStockQty()).contains(text)
                || String.valueOf(it.getUnitPrice()).contains(text)
                || it.getActualCurrency().toLowerCase().contains(text)
                || String.valueOf(it.getCurrencyRate()).contains(text)));
    }

    private ComputeContext pickComputingItem() {
        String compute = computeBy.getSelectionModel().getSelectedItem();
        return (compute.equals(lang.getProperty("price")))
                ? ComputeContext.PRICE : ComputeContext.QUANTITY;
    }

    private ChartContext pickGroupingItem() {
        String chart = groupBy.getSelectionModel().getSelectedItem();
        if (chart.equals(lang.getProperty("month"))) return ChartContext.MONTH;
        else if (chart.equals(lang.getProperty("day_month"))) return ChartContext.DAY_OF_MONTH;
        else if (chart.equals(lang.getProperty("day_week"))) return ChartContext.DAY_OF_WEEK;
        else return ChartContext.YEAR;
    }
    
    private void setDatePickerValueProperty(JFXDatePicker picker) {
        picker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                if (PeriodValidator.isValid(from.getValue(), to.getValue())) {
                    searchText.clear();
                    queryLogsFromPeriod();
                    querySaleLigns(PeriodContext.PERIOD);
                }else {
                    picker.setValue(oldValue);
                    Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_period"));
                }
        });
    }
    
    private void queryLogsFromPeriod() {
        List<ProductLog> logs = productLogService
                .getByProduct(product, from.getValue(), to.getValue());
        addLogsToTable(logs);
    }

    private void addLogsToTable(List<ProductLog> logs) {
        FilteredList<ProductLog> items = new FilteredList<>(FXCollections
                .observableArrayList(logs), data -> true);
        SortedList<ProductLog> sortedItems = new SortedList<>(items);
        sortedItems.comparatorProperty().bind(productLogsTable.comparatorProperty());
        productLogsTable.refresh();
        productLogsTable.setItems(sortedItems);
        setSearchFieldTextProperty(items);
    }

    private void querySaleLigns(PeriodContext context) {
        ligns = context.equals(PeriodContext.PERIOD) ? saleLineService
                .getFromProduct(product, from.getValue(), to.getValue())
                : saleLineService.getFromProduct(product);
        chartSaleLigns(ligns);
    }
    
    private void setAllEventHandler() {
        all.setOnAction(e -> {
            searchText.clear();
            managePeriods();
        });
    }
    
    private void chartSaleLigns(List<SaleLine> logs) {
        ChartContext group = pickGroupingItem();
        ComputeContext comp = pickComputingItem();
        BarChart<String, Number> chart = SaleLineCharter.chart(logs, group, comp);
        chartBox.getChildren().clear();
        chartBox.getChildren().add(chart);
    }
    
    private void setAllTooltip(PeriodContext tooltip) {
        if (tooltip.equals(PeriodContext.ALL)) {
            all.setTooltip(ControlsHandler.createTooltip("#2E4D7D"
                    , lang.getProperty("find_all")));
        } else {
            all.setTooltip(ControlsHandler.createTooltip("#2E4D7D"
                    , lang.getProperty("find_by_period")));
        }
    }

    private void managePeriods() {
        if (from.isDisabled() && to.isDisabled()) {
            from.setDisable(false);
            to.setDisable(false);
            queryLogsFromPeriod();
            querySaleLigns(PeriodContext.PERIOD);
            setAllTooltip(PeriodContext.ALL);
        } else {
            from.setDisable(true);
            to.setDisable(true);
            addLogsToTable(productLogService.getByProduct(product));
            querySaleLigns(PeriodContext.ALL);
            setAllTooltip(PeriodContext.PERIOD);
        }
    }
    
    private enum PeriodContext {ALL, PERIOD}
    
}

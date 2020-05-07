/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.exporters.ProductLogExporter;
import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.ProductLog;
import com.elikya.apps.rhoe.desk.service.ProductLogService;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import com.elikya.apps.rhoe.desk.util.DayOfWeekPicker;
import com.elikya.apps.rhoe.desk.util.PeriodValidator;
import com.elikya.apps.rhoe.desk.util.TableViewOperation;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

@Component
public class LogsResumeController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private CustomTextField searchField;
    @FXML private JFXButton searchBtn;
    @FXML private TableView<ProductLog> logsTable;
    @FXML private TableColumn<ProductLog, LocalDate> logDate;
    @FXML private TableColumn<ProductLog, LocalTime> logTime;
    @FXML private TableColumn<ProductLog, Product> productName;
    @FXML private TableColumn<ProductLog, Product> productSerialNumber;
    @FXML private TableColumn<ProductLog, Product> productNumber;
    @FXML private TableColumn<ProductLog, String> action;
    @FXML private TableColumn<ProductLog, Integer> actionQty;
    @FXML private TableColumn<ProductLog, BigDecimal> actionPrice;
    @FXML private TableColumn<ProductLog, BigDecimal> totalPrice;
    @FXML private TableColumn<ProductLog, String> actionCurrency;
    @FXML private TableColumn<ProductLog, BigDecimal> currencyRate;
    @FXML private TableColumn<ProductLog, Integer> stockQty;
    @FXML private TableColumn<ProductLog, String> reason;
    @FXML private MenuItem export;
    @FXML private JFXDatePicker from;
    @FXML private JFXDatePicker to;
    @FXML private JFXButton all;

    private Properties lang;

    private static List<Integer> ids;

    public static void setIds(List<Integer> _ids) {
        ids = _ids;
    }

    private ProductLogService productLogService;

    @Autowired
    private void setProductLogService(ProductLogService productLogService) {
        this.productLogService = productLogService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableViewOperation.handleSelection(logsTable);
        TableViewOperation.setTableSelectionModel(logsTable);
        ControlsHandler.handleSearchZone(searchField, searchBtn);
        setLanguage();
        setColumnsCellsValuesFactories();
        setColumnsCellsFactories();
        setAllTooltip(SearchContext.ALL);
        setDatesValues();
        setLogsInTable(SearchContext.PERIOD);
        setCloseEventHandler();
        setAllEventHandler();
        setExportEventHandler();
        setDatePickerValueProperty(from);
        setDatePickerValueProperty(to);
    }

    private void setLanguage() {
        lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty("products_resume"));
        from.setPromptText(lang.getProperty("from"));
        to.setPromptText(lang.getProperty("to"));
        searchField.setPromptText(lang.getProperty("search"));
        logsTable.setPlaceholder(new Label(lang.getProperty("table_prompt")));
        logDate.setText(lang.getProperty("date"));
        logTime.setText(lang.getProperty("time"));
        productName.setText(lang.getProperty("name"));
        productSerialNumber.setText(lang.getProperty("serial_number"));
        productNumber.setText(lang.getProperty("product_number"));
        action.setText(lang.getProperty("action"));
        actionQty.setText(lang.getProperty("quantity"));
        String currencySymbol = " (" + ApplicationCurrency.getActualCurrency() + ")";
        actionPrice.setText(lang.getProperty("unit_price_tax") + currencySymbol);
        totalPrice.setText(lang.getProperty("total_price_tax") + currencySymbol);
        actionCurrency.setText(lang.getProperty("move_currency"));
        currencyRate.setText(lang.getProperty("move_rate"));
        stockQty.setText(lang.getProperty("stock_quantity"));
        reason.setText(lang.getProperty("reason"));
        export.setText(lang.getProperty("export"));
    }

    private void setColumnsCellsValuesFactories() {
        logDate.setCellValueFactory(new PropertyValueFactory<>("logDate"));
        logTime.setCellValueFactory(new PropertyValueFactory<>("logTime"));
        productName.setCellValueFactory(new PropertyValueFactory<>("product"));
        productSerialNumber.setCellValueFactory(new PropertyValueFactory<>("product"));
        productNumber.setCellValueFactory(new PropertyValueFactory<>("product"));
        action.setCellValueFactory(new PropertyValueFactory<>("logAction"));
        actionQty.setCellValueFactory(new PropertyValueFactory<>("actionQty"));
        actionPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        actionCurrency.setCellValueFactory(new PropertyValueFactory<>("actualCurrency"));
        currencyRate.setCellValueFactory(new PropertyValueFactory<>("currencyRate"));
        stockQty.setCellValueFactory(new PropertyValueFactory<>("stockQty"));
        reason.setCellValueFactory(new PropertyValueFactory<>("reason"));
    }

    private void setColumnsCellsFactories() {
        setProductNameCellFactory();
        setProductSerialNumberCellFactory();
        setProductNumberCellFactory();
        setActionCellFactory();
    }

    private void setProductNameCellFactory() {
        productName.setCellFactory(param -> new TableCell<ProductLog, Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                if (!empty)
                    setText(item.getLabel());
            }
        });
    }

    private void setProductSerialNumberCellFactory() {
        productSerialNumber.setCellFactory(param -> new TableCell<ProductLog, Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                if (!empty)
                    setText(item.getSerialNumber());
            }
        });
    }

    private void setProductNumberCellFactory() {
        productNumber.setCellFactory(param -> new TableCell<ProductLog, Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                if (!empty)
                    setText(item.getNumber());
            }
        });
    }

    private void setActionCellFactory() {
        action.setCellFactory(param -> new TableCell<ProductLog, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (!empty)
                    setText(lang.getProperty(item));
            }
        });
    }

    private void setAllTooltip(SearchContext searchContext) {
        String text;
        if (searchContext.equals(SearchContext.ALL)) text = "find_all";
        else text = "find_by_period";
        all.setTooltip(ControlsHandler.createTooltip("#2E4D7D", lang.getProperty(text)));
    }

    private void setDatesValues() {
        from.setValue(DayOfWeekPicker.getFirstDayOfWeek());
        to.setValue(DayOfWeekPicker.getActualDay());
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setAllEventHandler() {
        all.setOnAction(event -> {
            searchField.clear();
            if (datesPickersDisabled()) {
                disableDatePickers(false);
                setAllTooltip(SearchContext.PERIOD);
                setLogsInTable(SearchContext.PERIOD);
            } else {
                disableDatePickers(true);
                setAllTooltip(SearchContext.ALL);
                setLogsInTable(SearchContext.ALL);
            }
        });
    }

    private void setExportEventHandler() {
        export.setOnAction(event -> {
            List<ProductLog> logs = logsTable.getSelectionModel().getSelectedItems();
            if (!logs.isEmpty()) {
                ProductLogExporter.export(logs);
            }
        });
    }

    private boolean datesPickersDisabled() {
        return from.isDisabled() && to.isDisabled();
    }

    private void disableDatePickers(boolean value) {
        from.setDisable(value);
        to.setDisable(value);
    }

    private void setDatePickerValueProperty(JFXDatePicker picker) {
        picker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                if (PeriodValidator.isValid(from.getValue(), to.getValue())) {
                    searchField.clear();
                    setLogsInTable(SearchContext.PERIOD);
                }else {
                    picker.setValue(oldValue);
                    Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_period"));
                }
        });
    }


    private void setLogsInTable(SearchContext context) {
        List<ProductLog> logs = queryLogs(context);
        FilteredList<ProductLog> filteredLogs = filterLogs(logs);
        SortedList<ProductLog> sortedLogs = sortLogs(filteredLogs);
        logsTable.refresh();
        logsTable.setItems(sortedLogs);
        setSearchFieldTextProperty(filteredLogs);
    }

    private List<ProductLog> queryLogs(SearchContext context) {
        return context.equals(SearchContext.ALL) ? productLogService.getAllByIds(ids)
                : productLogService.getByIdsBetweenDates(ids, from.getValue(), to.getValue());
    }

    private FilteredList<ProductLog> filterLogs(List<ProductLog> logs) {
        return new FilteredList<>(FXCollections.observableArrayList(logs), data -> true);
    }

    private SortedList<ProductLog> sortLogs(FilteredList<ProductLog> logs) {
        SortedList<ProductLog> sortedLogs = new SortedList<>(logs);
        sortedLogs.comparatorProperty().bind(logsTable.comparatorProperty());
        return sortedLogs;
    }

    private void setSearchFieldTextProperty(FilteredList<ProductLog> logs) {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty())
                logs.setPredicate(p -> true);
            else {
                String text = newValue.toLowerCase();
                searchTextInLogs(text, logs);
                logsTable.refresh();
            }
        });
    }

    private void searchTextInLogs(String text, FilteredList<ProductLog> logs) {
        Platform.runLater(() -> logs.setPredicate(it
                -> it.getLogDate().toString().contains(text)
                || it.getLogTime().toString().contains(text)
                || it.getProduct().getLabel().contains(text)
                || it.getProduct().getSerialNumber().toLowerCase().contains(text)
                || it.getProduct().getNumber().toLowerCase().contains(text)
                || lang.getProperty(it.getLogAction()).toLowerCase().contains(text)
                || it.getReason().toLowerCase().contains(text)
                || it.getActualCurrency().toLowerCase().contains(text)
                || String.valueOf(it.getActionQty()).contains(text)
                || String.valueOf(it.getUnitPrice()).contains(text)
                || String.valueOf(it.getTotalPrice()).contains(text)
                || String.valueOf(it.getCurrencyRate()).contains(text)
                || String.valueOf(it.getStockQty()).contains(text)));
    }

    private enum SearchContext {PERIOD, ALL}

}

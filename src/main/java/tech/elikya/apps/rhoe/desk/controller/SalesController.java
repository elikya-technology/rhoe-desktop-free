/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.elikya.apps.rhoe.desk.chart.ChartsUtils.ComputeContext;
import tech.elikya.apps.rhoe.desk.chart.SalesCharter;
import tech.elikya.apps.rhoe.desk.chart.SalesCharter.ChartContext;
import tech.elikya.apps.rhoe.desk.entity.Sale;
import tech.elikya.apps.rhoe.desk.exporters.SaleExporter;
import tech.elikya.apps.rhoe.desk.observers.impl.CRUDMasterImpl;
import tech.elikya.apps.rhoe.desk.observers.impl.CurrencyObserverImpl;
import tech.elikya.apps.rhoe.desk.observers.impl.DecimalsObserverImpl;
import tech.elikya.apps.rhoe.desk.observers.impl.LanguageObserverImpl;
import tech.elikya.apps.rhoe.desk.observers.interfaces.CRUDMaster;
import tech.elikya.apps.rhoe.desk.observers.interfaces.CurrencyObserver;
import tech.elikya.apps.rhoe.desk.observers.interfaces.DecimalsObserver;
import tech.elikya.apps.rhoe.desk.observers.interfaces.LanguageObserver;
import tech.elikya.apps.rhoe.desk.service.SaleService;
import tech.elikya.apps.rhoe.desk.ui.*;
import tech.elikya.apps.rhoe.desk.util.*;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class SalesController implements Initializable, LanguageObserver
        , CurrencyObserver, DecimalsObserver, CRUDMaster {

    @FXML private JFXButton add;
    @FXML private JFXComboBox<String> groupBy;
    @FXML private MenuItem delete;
    @FXML private TableColumn<Sale, LocalTime> time;
    @FXML private JFXDatePicker from;
    @FXML private JFXDatePicker to;
    @FXML private CustomTextField searchText;
    @FXML private TableColumn<Sale, LocalDate> date;
    @FXML private TableColumn<Sale, BigDecimal> price;
    @FXML private TableView<Sale> salesTable;
    @FXML private JFXButton all;
    @FXML private TableColumn<Sale, BigDecimal> taxedPrice;
    @FXML private TableColumn<Sale, String> number;
    @FXML private MenuItem export;
    @FXML private MenuItem reports;
    @FXML private MenuItem _details;
    @FXML private VBox generalBox;
    @FXML private VBox detailsBox;
    @FXML private Label labelPeriods;
    @FXML private Label labelProducts;
    @FXML private JFXButton searchBtn;
    @FXML private JFXButton taxes;
    @FXML private JFXComboBox<String> computeBy;
    
    private SaleService saleService;

    private Properties lang;
    private BarChart<String, Number> detailsChart;
    private List<Sale> salesList;
    private ComputeContext computeContext;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        computeContext = ComputeContext.PRICE;
        LanguageObserverImpl.register(this);
        CurrencyObserverImpl.register(this);
        DecimalsObserverImpl.register(this);
        CRUDMasterImpl.register(this);
        TableViewOperation.handleSelection(salesTable);
        TableViewOperation.setTableSelectionModel(salesTable);
        ControlsHandler.handleSearchZone(searchText, searchBtn);
        setControlsLanguage();
        setControlsTooltips();
        initControls();
    }
    
    @Autowired
    private void setSaleService(SaleService saleService) {
        this.saleService = saleService;
    }

    private void initControls() {
        setDatesValues();
        setGroupingItems();
        setComputeByItems();
        queryByPeriod();
        setNewSaleEventHandler();
        setTableCellsValuesFactories();
        setPriceCellFactory(price);
        setPriceCellFactory(taxedPrice);
        setDeleteEventHandler();
        setComputeByProperty();
        setTaxesEventHandler();
        setReportsEventHandler();
        setDetailsEventHandler();
        setGroupByEventHandler();
        setFromEventHandler();
        setAllEventHandler();
        setToValueProperty();
        setSalesTableClickHandler();
        setExportEventHandler();
        displayData();
    }

    @Override
    public void updateLanguage() {
        setControlsLanguage();
        setControlsTooltips();
        setGroupingItems();
        setComputeByItems();
        resetDataDisplayed();
        chartSales(salesList);
        chartProducts(salesList, ChartItemState.DISABLED);
    }

    @Override
    public void updateCurrency() {
        resetDataDisplayed();
        setCurrencySymbolBundle();
    }

    private void resetDataDisplayed() {
        setDatesValues();
        resetPeriodChooserControls();
        restorePeriodicQuery();
        displayData();
    }

    private void resetPeriodChooserControls() {
        if (datesFieldsAreDisabled()) {
            disableDateFields(false);
            setAllTooltipText("find_all");
        }
    }

    @Override
    public void updateDecimals() {
        displayData();
    }

    @Override
    public void deleteRecord() {
        Platform.runLater(() -> {
            List<Sale> items = salesTable.getSelectionModel().getSelectedItems();
            saleService.deleteAll(items);
            salesList.removeAll(items);
            salesTable.getSelectionModel().clearSelection();
            displayData();
            Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty("sale_deleted"));
        });
    }

    @Override
    public void addRecord() {
        salesList.add(saleService.getLast());
        displayData();
    }

    @Override
    public void updateRecord() {
        salesTable.refresh();
        ObservableList<Sale> items = salesTable.getItems();
        chartProducts(items, ChartItemState.DISABLED);
        chartSales(items);
    }

    private void putSalesInTable() {
        FilteredList<Sale> items = new FilteredList<>(FXCollections
                .observableArrayList(salesList), data -> true);
        SortedList<Sale> sortedItems = new SortedList<>(items);
        sortedItems.comparatorProperty().bind(salesTable.comparatorProperty());
        salesTable.setItems(sortedItems);
        salesTable.refresh();
        setSearchBtnTextProperty(items);
    }
    
    private void setDatesValues() {
        from.setValue(DayOfWeekPicker.getFirstDayOfWeek());
        to.setValue(DayOfWeekPicker.getActualDay());
    }
    
    private void setGroupingItems() {
        groupBy.setItems(FXCollections.observableArrayList(lang.getProperty("month"),
                lang.getProperty("day_month"), lang.getProperty("day_week"),
                lang.getProperty("year")));
        groupBy.getSelectionModel().selectFirst();
    }
    
    private void setComputeByItems() {
        computeBy.setItems(FXCollections.observableArrayList(lang.getProperty("price")
                , lang.getProperty("quantity")));
        computeBy.getSelectionModel().selectFirst();
    }
    
    private void setControlsLanguage() {
        lang = ControlsHandler.getLanguage();
        from.setPromptText(lang.getProperty("from"));
        to.setPromptText(lang.getProperty("to"));
        searchText.setPromptText(lang.getProperty("search"));
        time.setText(lang.getProperty("time"));
        date.setText(lang.getProperty("date"));
        groupBy.setPromptText(lang.getProperty("group_by"));
        salesTable.setPlaceholder(new Label(lang.getProperty("table_prompt")));
        _details.setText(lang.getProperty("resume"));
        delete.setText(lang.getProperty("delete"));
        from.setPromptText(lang.getProperty("from"));
        to.setPromptText(lang.getProperty("to"));
        number.setText(lang.getProperty("sale_number"));
        export.setText(lang.getProperty("export"));
        labelPeriods.setText(lang.getProperty("periods"));
        labelProducts.setText(lang.getProperty("products"));
        computeBy.setPromptText(lang.getProperty("compute_by"));
        reports.setText(lang.getProperty("reports"));
        setCurrencySymbolBundle();
    }

    private void setCurrencySymbolBundle() {
        String currency = ApplicationCurrency.getActualCurrency();
        taxedPrice.setText(lang.getProperty("total_price_tax") + " (" +
                currency + ")");
        price.setText(lang.getProperty("total_price")+ " (" +
                currency + ")");
    }

    private void setControlsTooltips() {
        add.setTooltip(ControlsHandler.createTooltip("#9C27B0", lang.getProperty("new_sale")));
        taxes.setTooltip(ControlsHandler.createTooltip("#0277BD", lang.getProperty("taxes")));
        setAllTooltipText("find_all");
    }

    private void setTableCellsValuesFactories() {
        number.setCellValueFactory(new PropertyValueFactory<>("number"));
        date.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        time.setCellValueFactory(new PropertyValueFactory<>("saleTime"));
        price.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        taxedPrice.setCellValueFactory(new PropertyValueFactory<>("taxedPrice"));
    }

    private void setPriceCellFactory(TableColumn<Sale, BigDecimal> column) {
        column.setCellFactory(param -> new TableCell<Sale, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty) {
                    setText(NumbersFormatter.getFormattedString(item));
                }
            }
        });
    }

    private void setReportsEventHandler() {
        reports.setOnAction(e -> {
            List<Sale> items = salesTable.getSelectionModel().getSelectedItems();
            if (!items.isEmpty()) {
                ReportsController.setSalesIds(extractSalesIds(items));
                Stages.showResponsiveDialog(StagesPaths.REPORTS, StageSize.LARGER);
            }
        });
    }

    private List<Integer> extractSalesIds(List<Sale> items) {
        return items.stream().map(Sale::getId).distinct().collect(Collectors.toList());
    }

    private void setNewSaleEventHandler() {
        add.setOnAction(e -> Stages.showResponsiveDialog(StagesPaths.NEW_SALE, StageSize.LARGER));
    }
    
    private void setTaxesEventHandler() {
        taxes.setOnAction(e -> Stages.showResponsiveDialog(StagesPaths.TAXES, StageSize.LARGER));
    }
    
    private void setDeleteEventHandler() {
        delete.setOnAction(e -> Stages.showDialog(StagesPaths.DELETION_DIALOG));
    }
    
    private void setDetailsEventHandler() {
        _details.setOnAction(e -> pickSelectedSale());
    }
    
    private void setExportEventHandler() {
        export.setOnAction(e -> Platform.runLater(() -> {
            List<Sale> items = salesTable.getSelectionModel().getSelectedItems();
            if (!items.isEmpty())
                SaleExporter.export(items);
        }));
    }

    private void pickSelectedSale() {
        Optional<Sale> selectedItem = Optional.ofNullable(
                salesTable.getSelectionModel().getSelectedItem());
        selectedItem.ifPresent(item -> {
            SaleDetailsController.setSale(item);
            Stages.showResponsiveDialog(StagesPaths.SALE_DETAILS, StageSize.LARGER);
        });
    }
    
    private void setSalesTableClickHandler() {
        salesTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2)
                pickSelectedSale();
        });
    }
    
    private void setAllEventHandler() {
        all.setOnAction(e -> Platform.runLater(() -> {
            searchText.clear();
            handleQuerying();
            displayData();
        }));
    }

    private void displayData() {
        putSalesInTable();
        chartSales(salesList);
        chartProducts(salesList, ChartItemState.DISABLED);
    }

    private void handleQuerying() {
        if (datesFieldsAreDisabled()) {
            restorePeriodicQuery();
        } else {
            queryAllTheData();
        }
    }

    private boolean datesFieldsAreDisabled() {
        return from.isDisabled() && to.isDisabled();
    }

    private void queryAllTheData() {
        disableDateFields(true);
        setAllTooltipText("find_by_period");
        salesList = saleService.getAll();
    }

    private void setAllTooltipText(String text) {
        all.setTooltip(ControlsHandler.createTooltip("#2E4D7D"
                , lang.getProperty(text)));
    }

    private void restorePeriodicQuery() {
        disableDateFields(false);
        queryByPeriod();
        setAllTooltipText("find_all");
    }

    private void disableDateFields(boolean b) {
        from.setDisable(b);
        to.setDisable(b);
    }

    private void setGroupByEventHandler() {
        groupBy.setOnAction(e -> {
            chartSales(salesList);
            chartProducts(salesList, ChartItemState.DISABLED);
        });
    }
    
    private void setComputeByProperty() {        
        computeBy.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                computeContext= newValue.equals(lang.getProperty("price"))
                        ? ComputeContext.PRICE : ComputeContext.QUANTITY;
                chartSales(salesList);
                chartProducts(salesList, ChartItemState.ENABLED);
            }
        });
    }
            
    private void setFromEventHandler() {setDatePickerValueProperty(from);}
    
    private void setToValueProperty() {setDatePickerValueProperty(to);}
    
    private void setDatePickerValueProperty(JFXDatePicker picker) {
        picker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                if (PeriodValidator.isValid(from.getValue(), to.getValue())) {
                    searchText.clear();
                    queryByPeriod();
                    displayData();
                }
                else {
                    picker.setValue(oldValue);
                    Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_period"));
                }
        });
    }
    
    private void queryByPeriod() {
        salesList = saleService.getFromPeriod(from.getValue(), to.getValue());
    }
    
    private void chartProducts(List<Sale> sales, ChartItemState state) {
        detailsChart = SalesCharter.chartSales(ChartContext.PRODUCTS, sales, computeContext);
        detailsBox.getChildren().clear();
        detailsBox.getChildren().add(detailsChart);
        setDetailsChartEventHandler(state);
    }
    
    private void chartSales(List<Sale> sales) {
        String period = groupBy.getSelectionModel().getSelectedItem();
        if (period != null) {
            BarChart<String, Number> generalChart;
            if (period.equals(lang.getProperty("month")))
                generalChart = SalesCharter.chartSales(ChartContext.MONTH, sales, computeContext);
            else if (period.equals(lang.getProperty("day_week")))
                generalChart = SalesCharter.chartSales(ChartContext.DAY_OF_WEEK, sales, computeContext);
            else if (period.equals(lang.getProperty("day_month")))
                generalChart = SalesCharter.chartSales(ChartContext.DAY_OF_MONTH, sales, computeContext);
            else
                generalChart = SalesCharter.chartSales(ChartContext.YEAR, sales, computeContext);
            generalBox.getChildren().clear();
            generalBox.getChildren().add(generalChart);
            setGeneralChartEventHandler(generalChart);
            salesTable.getSelectionModel().clearSelection();
        }
    }
    
    private void setDetailsChartEventHandler(ChartItemState state) {
        ContextMenu contextMenu = new ContextMenu();
        detailsChart.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem item = new MenuItem(lang.getProperty("previous"));
                item.setOnAction(e -> {
                    salesTable.getSelectionModel().clearSelection();
                    chartProducts(salesList, ChartItemState.DISABLED);
                });
                if (state.equals(ChartItemState.ENABLED)) item.setDisable(false);
                else item.setDisable(true);
                contextMenu.getItems().setAll(item);
                contextMenu.setHideOnEscape(true);
                contextMenu.show(detailsChart, event.getScreenX(), event.getScreenY());
            }
        });
        detailsChart.setOnMousePressed(event -> contextMenu.hide());
    }
    
    private void setGeneralChartEventHandler(BarChart<String, Number> chart) {
        chart.getData().forEach(data -> data.getData().forEach(item -> {
            Node node = item.getNode();
            node.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    salesTable.getSelectionModel().clearSelection();
                    selectFromPeriod(item);
                    chartProducts(salesTable.getSelectionModel()
                            .getSelectedItems(), ChartItemState.ENABLED);
                }
            });
        }));
    }
    
    private void selectFromPeriod(XYChart.Data<String, Number> data) {
        String period = groupBy.getSelectionModel().getSelectedItem();
        Stream<Sale> stream = salesList.stream();
        if (period.equals(lang.getProperty("month")))
            stream.filter(i -> (Integer)data
                    .getExtraValue() == i.getSaleDate().getMonth().getValue())
                    .forEach((obj) -> salesTable.getSelectionModel().select(obj));
        else if (period.equals(lang.getProperty("day_week")))
            stream.filter(i -> (Integer) data
                    .getExtraValue() == i.getSaleDate().getDayOfWeek().getValue())
                    .forEach(salesTable.getSelectionModel()::select);
        else if (period.equals(lang.getProperty("day_month")))
            stream.filter(i -> (Integer) data
                    .getExtraValue() == i.getSaleDate().getDayOfMonth())
                    .forEach(salesTable.getSelectionModel()::select);
        else
            stream.filter(i -> (Integer) data
                    .getExtraValue() == i.getSaleDate().getYear())
                    .forEach(salesTable.getSelectionModel()::select);
    }

    private void setSearchBtnTextProperty(FilteredList<Sale> items) {
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) items.setPredicate(p -> true);
            else {
                String text = newValue.toLowerCase();
                filterProducts(items, text);
                salesTable.refresh();
            }
        });
    }

    private void filterProducts(FilteredList<Sale> items, String text) {
        Platform.runLater(() -> items.setPredicate(sal -> sal.getNumber().contains(text)
                || sal.getSaleDate().toString().contains(text)
                || sal.getSaleTime().toString().contains(text)
                || String.valueOf(sal.getTaxedPrice()).contains(text)
                || String.valueOf(sal.getTotalPrice()).contains(text)));
    }

    private enum ChartItemState {ENABLED, DISABLED}
    
}

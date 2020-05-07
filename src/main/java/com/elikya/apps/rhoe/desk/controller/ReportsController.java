/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.exporters.SaleLineExporter;
import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.Sale;
import com.elikya.apps.rhoe.desk.entity.SaleLine;
import com.elikya.apps.rhoe.desk.service.SaleLineService;
import com.elikya.apps.rhoe.desk.service.SaleService;
import com.elikya.apps.rhoe.desk.observers.impl.ValidationObserverImpl;
import com.elikya.apps.rhoe.desk.observers.interfaces.ValidationObserver;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.NumbersFormatter;
import com.elikya.apps.rhoe.desk.util.Configs;
import com.elikya.apps.rhoe.desk.util.TableViewOperation;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class ReportsController implements Initializable, ValidationObserver {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private CustomTextField searchField;
    @FXML private JFXButton searchBtn;
    @FXML private TableView<SaleLine> reportsTable;
    @FXML private TableColumn<SaleLine, Sale> saleNumber;
    @FXML private TableColumn<SaleLine, Sale> saleDate;
    @FXML private TableColumn<SaleLine, Sale> saleTime;
    @FXML private TableColumn<SaleLine, Product> prodName;
    @FXML private TableColumn<SaleLine, Product> prodSerNumber;
    @FXML private TableColumn<SaleLine, Product> prodNumber;
    @FXML private TableColumn<SaleLine, Integer> quantity;
    @FXML private TableColumn<SaleLine, BigDecimal> price;
    @FXML private TableColumn<SaleLine, BigDecimal> prodUnitPrice;
    @FXML private MenuItem export;
    @FXML private MenuItem _exportAll;
    @FXML private JFXTextField salesNumber;
    @FXML private JFXTextField productsNumber;
    @FXML private JFXTextField productQty;
    @FXML private JFXTextField totalPriceET;
    @FXML private JFXTextField totalPriceIT;
    @FXML private MenuItem deleteSale;

    private SaleService saleService;
    private SaleLineService saleLineService;

    private static List<Integer> salesIds;

    private Properties lang;
    private String currency;
    private List<Sale> sales;
    private List<SaleLine> lines;
    private List<Integer> salesIdsOfSelectedLines;
    private ObservableList<SaleLine> selectedLines;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ValidationObserverImpl.register(this);
        lang = ControlsHandler.getLanguage();
        currency = Configs.get().getProperty("currency");
        getSelectedIdsSales();
        ControlsHandler.handleSearchZone(searchField, searchBtn);
        TableViewOperation.handleSelection(reportsTable);
        TableViewOperation.setTableSelectionModel(reportsTable);
        setCloseEventHandler();
        setExportAllEventHandler();
        setExportEventHandler();
        setDeleteSaleEventHandler();
        setCellsValuesFactory();
        setLanguage();
        putItemsOnTable();
        setCellsFactories();
        computeReportDetails();
        setPriceCellFactory();
        setProdUnitPriceCellFactory();
    }

    @Override
    public void processDeletionValidation() {
        saleLineService.deleteAll(selectedLines);
        saleService.deleteFromIds(salesIds);
        lines.removeAll(selectedLines);
        getSelectedIdsSales();
        putItemsOnTable();
        ValidationObserverImpl.processDeleteOnFirstRegistered();
    }

    @Override
    public void processUpdateValidation() {}

    @Autowired
    private void setSaleService(SaleService saleService) {
        this.saleService = saleService;
    }

    @Autowired
    private void setSaleLineService(SaleLineService saleLineService) {
        this.saleLineService = saleLineService;
    }

    private void setLanguage() {
        title.setText(lang.getProperty("reports"));
        prodName.setText(lang.getProperty("name"));
        prodSerNumber.setText(lang.getProperty("serial_number"));
        prodNumber.setText(lang.getProperty("product_number"));
        price.setText(lang.getProperty("price") + " (" + currency + ")");
        quantity.setText(lang.getProperty("quantity"));
        saleNumber.setText(lang.getProperty("sale_number"));
        searchField.setPromptText(lang.getProperty("search"));
        totalPriceET.setPromptText(lang.getProperty("total_price") + " (" + currency + ")");
        totalPriceIT.setPromptText(lang.getProperty("total_price_tax") + " (" + currency + ")");
        salesNumber.setPromptText(lang.getProperty("sales_number"));
        productQty.setPromptText(lang.getProperty("products_quantity"));
        productsNumber.setPromptText(lang.getProperty("products_number"));
        reportsTable.setPlaceholder(new Label(lang.getProperty("table_prompt")));
        export.setText(lang.getProperty("export"));
        _exportAll.setText(lang.getProperty("export_all"));
        saleDate.setText(lang.getProperty("date"));
        saleTime.setText(lang.getProperty("time"));
        deleteSale.setText(lang.getProperty("delete_sale"));
        prodUnitPrice.setText(lang.getProperty("unit_price_tax"));
    }

    public static void setSalesIds(List<Integer> items) {
        salesIds = items;
    }

    private void getSelectedIdsSales() {
        sales = saleService.getFromIds(salesIds);
    }

    private void setCloseEventHandler() {
        close.setOnAction(event -> {
            ValidationObserverImpl.unregister(this);
            Stages.close(event);
        });
    }

    private void setExportEventHandler() {
        export.setOnAction(e -> Platform.runLater(() -> {
            List<SaleLine> items = reportsTable.getSelectionModel().getSelectedItems();
            if (!items.isEmpty()) SaleLineExporter.export(items);
        }));
    }

    private void setExportAllEventHandler() {
        _exportAll.setOnAction(e -> {
            List<SaleLine> items = reportsTable.getItems();
            if (!items.isEmpty()) SaleLineExporter.export(items);
        });
    }

    private void setSearchFieldEventHandler(FilteredList<SaleLine> items) {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) items.setPredicate(p -> true);
            else {
                String text = newValue.toLowerCase();
                filterReport(items, text);
                reportsTable.refresh();
            }
        });
    }

    private void setDeleteSaleEventHandler() {
        deleteSale.setOnAction(event -> {
            selectedLines = reportsTable.getSelectionModel().getSelectedItems();
            salesIdsOfSelectedLines = getSalesIdsOfSelectedLines(selectedLines);
            selectFromLinesSales();
            Stages.showDialog(StagesPaths.DELETION_DIALOG);
        });
    }

    private List<Integer> getSalesIdsOfSelectedLines(List<SaleLine> selectedLines) {
        return selectedLines.stream().map(it -> it.getSale().getId()).distinct().collect(Collectors.toList());
    }

    private void selectFromLinesSales() {
        reportsTable.getItems().forEach(it -> salesIdsOfSelectedLines.forEach(rec -> {
            if (rec.equals(it.getSale().getId())) {
                reportsTable.getSelectionModel().select(it);
            }
        }));
    }

    private void filterReport(FilteredList<SaleLine> items, String text) {
        Platform.runLater(() -> items.setPredicate(it ->
                it.getProduct().getLabel().toLowerCase().contains(text)
                || it.getProduct().getNumber().toLowerCase().contains(text)
                || it.getProduct().getSerialNumber().toLowerCase().contains(text)
                || it.getSale().getNumber().contains(text)
                || it.getSale().getSaleDate().toString().contains(text)
                || it.getSale().getSaleTime().toString().contains(text)
                || String.valueOf(it.getQuantity()).contains(text)
                || String.valueOf(it.getPrice()).contains(text)
                || String.valueOf(it.getUnitPrice()).contains(text)));
    }

    private void putItemsOnTable() {
        lines = extractLigns();
        FilteredList<SaleLine> filteredList = new FilteredList<>(FXCollections
                .observableArrayList(lines), data -> true);
        SortedList<SaleLine> sortedItems = new SortedList<>(filteredList);
        sortedItems.comparatorProperty().bind(reportsTable.comparatorProperty());
        reportsTable.setItems(sortedItems);
        reportsTable.refresh();
        setSearchFieldEventHandler(filteredList);
    }

    private void setCellsValuesFactory() {
        saleNumber.setCellValueFactory(new PropertyValueFactory<>("sale"));
        saleDate.setCellValueFactory(new PropertyValueFactory<>("sale"));
        saleTime.setCellValueFactory(new PropertyValueFactory<>("sale"));
        prodName.setCellValueFactory(new PropertyValueFactory<>("product"));
        prodSerNumber.setCellValueFactory(new PropertyValueFactory<>("product"));
        prodNumber.setCellValueFactory(new PropertyValueFactory<>("product"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        prodUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
    }

    private void setPriceCellFactory() {
        price.setCellFactory(param -> new TableCell<SaleLine, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty) setText(NumbersFormatter.getFormattedString(item));
            }
        });
    }

    private void setProdUnitPriceCellFactory() {
        prodUnitPrice.setCellFactory(param -> new TableCell<SaleLine, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty) setText(NumbersFormatter.getFormattedString(item));
            }
        });
    }

    private void setCellsFactories() {
        handleProductCellFactory(prodName, ProductItemContext.NAME);
        handleProductCellFactory(prodNumber, ProductItemContext.PRODUCT_NUMBER);
        handleProductCellFactory(prodSerNumber, ProductItemContext.SERIAL_NUMBER);
        handleSaleCellFactory(saleDate, SaleItemContext.DATE);
        handleSaleCellFactory(saleTime, SaleItemContext.TIME);
        handleSaleCellFactory(saleNumber, SaleItemContext.SALE_NUMBER);
    }

    private void handleSaleCellFactory(TableColumn<SaleLine, Sale> column, SaleItemContext context) {
        column.setCellFactory(param -> new TableCell<SaleLine, Sale>() {
            @Override
            protected void updateItem(Sale item, boolean empty) {
                if (!empty) {
                    switch (context) {
                        case DATE: setText(item.getSaleDate().toString());break;
                        case SALE_NUMBER: setText(item.getNumber());break;
                        case TIME: setText(item.getSaleTime().toString());break;
                        default: break;
                    }
                }
            }
        });
    }

    private void handleProductCellFactory(TableColumn<SaleLine, Product> column, ProductItemContext context) {
        column.setCellFactory(param -> new TableCell<SaleLine, Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                if (!empty) {
                    switch (context) {
                        case NAME: setText(item.getLabel());break;
                        case SERIAL_NUMBER: setText(item.getSerialNumber());break;
                        case PRODUCT_NUMBER: setText(item.getNumber());break;
                        default:break;
                    }
                }
            }
        });
    }

    private List<SaleLine> extractLigns() {
        return sales.stream().flatMap(i -> i.getLines().stream()).collect(Collectors.toList());
    }

    private void computeReportDetails() {
        totalPriceIT.setText(NumbersFormatter.getFormattedString(computeTotalPriceIT()));
        totalPriceET.setText(NumbersFormatter.getFormattedString(computeTotalPriceET()));
        productsNumber.setText(String.valueOf(computeProductsNumber()));
        productQty.setText(String.valueOf(computeProductsQty()));
        salesNumber.setText(String.valueOf(salesIds.size()));

    }

    private BigDecimal computeTotalPriceET() {
        return sales.stream().map(Sale::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal computeTotalPriceIT() {
        return sales.stream().map(Sale::getTaxedPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int computeProductsNumber() {
        return lines.stream().map(SaleLine::getProduct).collect(Collectors.toSet()).size();
    }

    private int computeProductsQty() {
        return lines.stream().mapToInt(SaleLine::getQuantity).sum();
    }

    private enum SaleItemContext {DATE, TIME, SALE_NUMBER}

    private enum ProductItemContext {PRODUCT_NUMBER, NAME, SERIAL_NUMBER}

}

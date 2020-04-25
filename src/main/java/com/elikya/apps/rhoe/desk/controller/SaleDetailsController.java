/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.chart.ChartsUtils.ComputeContext;
import com.elikya.apps.rhoe.desk.chart.SaleLineCharter.ChartContext;
import com.elikya.apps.rhoe.desk.exporters.SaleLineExporter;
import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.Sale;
import com.elikya.apps.rhoe.desk.entity.SaleLine;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.SplitPaneDividerManager;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import com.elikya.apps.rhoe.desk.util.NumbersFormatter;
import com.elikya.apps.rhoe.desk.util.TableViewOperation;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import static com.elikya.apps.rhoe.desk.chart.SaleLineCharter.chart;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class SaleDetailsController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private JFXTextField dateField;
    @FXML private JFXTextField timeField;
    @FXML private JFXTextField sellingPriceField;
    @FXML private JFXTextField vatSellingPrice;
    @FXML private JFXTextField numberField;
    @FXML private TableColumn<SaleLine, Product> name;
    @FXML private TableColumn<SaleLine, Product> modelNumber;
    @FXML private TableColumn<SaleLine, Product> number;
    @FXML private TableColumn<SaleLine, Integer> quantity;
    @FXML private TableColumn<SaleLine, BigDecimal> price;
    @FXML private TableColumn<SaleLine, Product> category;
    @FXML private TableView<SaleLine> productsTable;
    @FXML private CustomTextField searchText;
    @FXML private MenuItem excel;
    @FXML private JFXTextField productsQuantity;
    @FXML private JFXTextField articlesQuantity;
    @FXML private TableColumn<SaleLine, Product> unitPrice;
    @FXML private JFXComboBox<String> computeBy;
    @FXML private Label labelChart;
    @FXML private VBox chartBox;
    @FXML private JFXButton searchBtn;
    @FXML private SplitPane splitPane;
    @FXML private JFXTextField moneyReceived;
    @FXML private JFXTextField difference;
    @FXML private JFXTextField actionCurrency;
    @FXML private JFXTextField currencyRate;
    
    public static Sale sale;
    private Properties lang;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lang = ControlsHandler.getLanguage();
        ControlsHandler.handleSearchZone(searchText, searchBtn);
        SplitPaneDividerManager.resize(splitPane);
        initControls();
        setLanguage();
        setTableCellValueFactory();
        fillData();
        handleTableData();
        setCloseEventHandler();
        addComputingItems();
        setUnitPriceCellFactory();
        chartLigns();
        setComputeByEventHandler();
        setExcelEventHandler();
        setPriceCellFactory();
    }
    
    public static void setSale(Sale s) {sale = s;}
    
    private void initControls() {
        TableViewOperation.setTableSelectionModel(productsTable);
        TableViewOperation.setSaleProductsTableCellFactory(TableViewOperation.FactoryContext.NAME, name);
        TableViewOperation.setSaleProductsTableCellFactory(TableViewOperation.FactoryContext.CATEGORY, category);
        TableViewOperation.setSaleProductsTableCellFactory(TableViewOperation.FactoryContext.PRODUCT_NUMBER, number);
        TableViewOperation.setSaleProductsTableCellFactory(TableViewOperation.FactoryContext.SERIAL_NUMBER, modelNumber);
    }

    private void setLanguage() {
        title.setText(lang.getProperty("sale_details"));
        dateField.setPromptText(lang.getProperty("date"));
        timeField.setPromptText(lang.getProperty("time"));
        sellingPriceField.setPromptText(lang.getProperty("total_price")
                + " (" + ApplicationCurrency.getActualCurrency() + ")");
        vatSellingPrice.setPromptText(lang.getProperty("total_price_tax")
                + " (" + ApplicationCurrency.getActualCurrency() + ")");
        numberField.setPromptText(lang.getProperty("sale_number"));
        name.setText(lang.getProperty("name"));
        modelNumber.setText(lang.getProperty("serial_number"));
        quantity.setText(lang.getProperty("quantity"));
        price.setText(lang.getProperty("price")
                + " (" + ApplicationCurrency.getActualCurrency() + ")");
        unitPrice.setText(lang.getProperty("unit_price_tax")
                + " (" + ApplicationCurrency.getActualCurrency() + ")");
        number.setText(lang.getProperty("product_number"));
        category.setText(lang.getProperty("category"));
        labelChart.setText(lang.getProperty("products"));
        excel.setText(lang.getProperty("export"));
        computeBy.setPromptText(lang.getProperty("compute_by"));
        searchText.setPromptText(lang.getProperty("search"));
        productsQuantity.setPromptText(lang.getProperty("products_number"));
        articlesQuantity.setPromptText(lang.getProperty("products_quantity"));
        productsTable.setPlaceholder(new Label(lang.getProperty("table_prompt")));
        moneyReceived.setPromptText(lang.getProperty("money_received") + " (" + ApplicationCurrency.getActualCurrency() + ")");
        difference.setPromptText(lang.getProperty("rest") + " (" + ApplicationCurrency.getActualCurrency() + ")");
        actionCurrency.setPromptText(lang.getProperty("currency"));
        currencyRate.setPromptText(lang.getProperty("conversion_value"));
    }
    
    @SuppressWarnings("DuplicatedCode")
    private void setTableCellValueFactory() {
        name.setCellValueFactory(new PropertyValueFactory<>("product"));
        modelNumber.setCellValueFactory(new PropertyValueFactory<>("product"));
        number.setCellValueFactory(new PropertyValueFactory<>("product"));
        category.setCellValueFactory(new PropertyValueFactory<>("product"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        unitPrice.setCellValueFactory(new PropertyValueFactory<>("product"));
    }

    private void setPriceCellFactory() {
        price.setCellFactory(param -> new TableCell<SaleLine, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty)
                    setText(NumbersFormatter.getFormattedString(item));
            }
        });
    }

    private void fillData() {
        if (sale != null) {
            dateField.setText(sale.getSaleDate().toString());
            timeField.setText(sale.getSaleTime().toString());
            numberField.setText(sale.getNumber());
            sellingPriceField.setText(NumbersFormatter.getFormattedString(sale.getTotalPrice()));
            vatSellingPrice.setText(NumbersFormatter.getFormattedString(sale.getTaxedPrice()));
            moneyReceived.setText(NumbersFormatter.getFormattedString(sale.getMoneyReceived()));
            BigDecimal diff = sale.getMoneyReceived().subtract(sale.getTaxedPrice());
            difference.setText(NumbersFormatter.getFormattedString(diff));
            actionCurrency.setText(sale.getCurrency());
            currencyRate.setText(NumbersFormatter.getFormattedString(getDetailsCurrency()));
        }
    }

    private BigDecimal getDetailsCurrency() {
        if (sale.getCurrency().equals(ApplicationCurrency.getDefaultCurrency()))
            return BigDecimal.valueOf(ApplicationCurrency.getActualRate());
        else return BigDecimal.valueOf(sale.getRate());
    }

    private void handleTableData() {
        if (sale != null) {
            FilteredList<SaleLine> items = filterSaleLines();
            SortedList<SaleLine> sortedItems = sortSaleLines(items);
            productsTable.refresh();
            productsTable.setItems(sortedItems);
            setSearchTextProperty(items);
            setProductsQuantities(items);
        }
    }

    private void setProductsQuantities(FilteredList<SaleLine> items) {
        productsQuantity.setText(String.valueOf(items.size()));
        articlesQuantity.setText(String.valueOf(computeArticles(items)));
    }

    private SortedList<SaleLine> sortSaleLines(FilteredList<SaleLine> items) {
        SortedList<SaleLine> sortedItems = new SortedList<>(items);
        sortedItems.comparatorProperty().bind(productsTable.comparatorProperty());
        return sortedItems;
    }

    private FilteredList<SaleLine> filterSaleLines() {
        return new FilteredList<>(FXCollections
                .observableArrayList(sale.getLines()), data -> true);
    }

    private void setCloseEventHandler() {
        close.setOnAction((event) -> {
            sale = null;
            Stages.close(event);
        });
    }

    private int computeArticles(List<SaleLine> items) {
        return items.stream().mapToInt(SaleLine::getQuantity).sum();
    }
    
    private void addComputingItems() {
        computeBy.getItems().addAll(FXCollections.observableArrayList(lang
                .getProperty("price"), lang.getProperty("quantity")));
        computeBy.getSelectionModel().selectFirst();
    }
    
    private BigDecimal computeUnitPrice(int id) {
        Optional<SaleLine> item = productsTable.getItems().stream()
                .filter(p -> p.getProduct().getId() == id).findAny();
        return item.map(saleLine -> saleLine.getPrice().divide(BigDecimal
                .valueOf(saleLine.getQuantity()), 3)).orElse(BigDecimal.ZERO);
    }
    
    private void setUnitPriceCellFactory() {
        unitPrice.setCellFactory(param -> new TableCell<SaleLine, Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    BigDecimal up = computeUnitPrice(item.getId());
                    setText(NumbersFormatter.getFormattedString(up));
                }
            }
        });
    }
    
    private void setComputeByEventHandler() {
        computeBy.setOnAction(e -> chartLigns());
    }
    
    private void setExcelEventHandler() {
        excel.setOnAction(e -> Platform.runLater(() -> {
            List<SaleLine> items = productsTable.getSelectionModel().getSelectedItems();
            if (!items.isEmpty())
                SaleLineExporter.export(items);
        }));
    }
    
    private void chartLigns() {
        String item = computeBy.getSelectionModel().getSelectedItem();
        List<SaleLine> lines = sale.getLines();
        BarChart<String, Number> chart = item.equals(lang.getProperty("price"))
                ? chart(lines, ChartContext.PRODUCTS, ComputeContext.PRICE)
                : chart(lines, ChartContext.PRODUCTS, ComputeContext.QUANTITY);
        chartBox.getChildren().clear();
        chartBox.getChildren().add(chart);
    }

    private void setSearchTextProperty(FilteredList<SaleLine> items) {
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) items.setPredicate(p -> true);
            else {
                String text = newValue.toLowerCase();
                filterLigns(items, text);
                productsTable.refresh();
            }
        });
    }

    private void filterLigns(FilteredList<SaleLine> items, String text) {
        Platform.runLater(() -> items.setPredicate(it
                -> String.valueOf(it.getPrice()).contains(text)
                || it.getProduct().getLabel().toLowerCase().contains(text)
                || it.getProduct().getSerialNumber().toLowerCase().contains(text)
                || it.getProduct().getNumber().toLowerCase().contains(text)
                || it.getProduct().getCategory().getLabel().toLowerCase().contains(text)
                || String.valueOf(it.getQuantity()).contains(text)
                || String.valueOf(it.getUnitPrice()).contains(text)));
    }

}

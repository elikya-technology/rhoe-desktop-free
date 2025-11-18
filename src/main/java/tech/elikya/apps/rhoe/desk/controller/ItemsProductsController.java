/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.controller;

import tech.elikya.apps.rhoe.desk.exporters.ProductExporter;
import tech.elikya.apps.rhoe.desk.entity.Category;
import tech.elikya.apps.rhoe.desk.entity.Product;
import tech.elikya.apps.rhoe.desk.entity.Provider;
import tech.elikya.apps.rhoe.desk.service.ProductService;
import tech.elikya.apps.rhoe.desk.ui.ControlsHandler;
import tech.elikya.apps.rhoe.desk.ui.Stages;
import tech.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import tech.elikya.apps.rhoe.desk.util.NumbersFormatter;
import tech.elikya.apps.rhoe.desk.util.TableViewOperation;
import com.jfoenix.controls.JFXButton;
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
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class ItemsProductsController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private TableView<Product> productsTable;
    @FXML private MenuItem excel;
    @FXML private TableColumn<Product, String> name;
    @FXML private TableColumn<Product, String> modelNumber;
    @FXML private TableColumn<Product, BigDecimal> price;
    @FXML private TableColumn<Product, Integer> realQuantity;
    @FXML private TableColumn<Product, Provider> provider;
    @FXML private TableColumn<Product, Category> category;
    @FXML private CustomTextField searchField;
    @FXML private JFXButton searchBtn;

    private String currency;
    private Properties lang;
    private static Object item;

    private ProductService productService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lang = ControlsHandler.getLanguage();
        currency = ApplicationCurrency.getActualCurrency();
        TableViewOperation.handleSelection(productsTable);
        ControlsHandler.handleSearchZone(searchField, searchBtn);
        setCloseEventHandler();
        setExcelEventHandler();
        setLanguage();
        setTableCellsValueFactory();
        setPriceCellFactory();
        setTableSelectionMode();
        spreadData();
    }

    public static void setItem(Object i) {
        item = i;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setLanguage() {
        title.setText(lang.getProperty("products"));
        searchField.setPromptText(lang.getProperty("search"));
        name.setText(lang.getProperty("name"));
        provider.setText(lang.getProperty("provider"));
        category.setText(lang.getProperty("category"));
        price.setText(lang.getProperty("unit_price") + " (" + currency + ")");
        realQuantity.setText(lang.getProperty("quantity"));
        productsTable.setPlaceholder(new Label(lang.getProperty("table_prompt")));
        excel.setText(lang.getProperty("export"));
        modelNumber.setText(lang.getProperty("serial_number"));
    }

    private void setTableCellsValueFactory() {
        name.setCellValueFactory(new PropertyValueFactory<>("label"));
        modelNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        price.setCellValueFactory(new PropertyValueFactory<>("convertedUnitPrice"));
        realQuantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        provider.setCellValueFactory(new PropertyValueFactory<>("provider"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
    }

    private void setPriceCellFactory() {
        price.setCellFactory(param -> new TableCell<Product, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty)
                    setText(NumbersFormatter.getFormattedString(item));
            }
        });
    }

    private void spreadData() {
        FilteredList<Product> items = filterProducts(getProducts());
        SortedList<Product> sortedItems = sortProducts(items);
        productsTable.refresh();
        productsTable.setItems(sortedItems);
        setSearchFieldProperty(items);
    }

    private SortedList<Product> sortProducts(FilteredList<Product> items) {
        SortedList<Product> sortedItems = new SortedList<>(items);
        sortedItems.comparatorProperty().bind(productsTable.comparatorProperty());
        return sortedItems;
    }

    private void setTableSelectionMode() {
        TableView.TableViewSelectionModel<Product> selectionModel
                = productsTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    private boolean isCategory() {
        return item instanceof Category;
    }

    private List<Product> getProducts() {
        return isCategory() ? ((Category) item).getProducts()
                : productService.getFromProvider(((Provider) item).getId());
    }

    private FilteredList<Product> filterProducts(List<Product> products) {
        return new FilteredList<>(FXCollections
                .observableArrayList(products), data -> true);
    }
    
    private void setSearchFieldProperty(FilteredList<Product> products) {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue.trim().isEmpty())
               products.setPredicate(p -> true);
           else {
               String text = newValue.toLowerCase();
               filterProducts(products, text);
               productsTable.refresh();
           }
        });
    }

    private void filterProducts(FilteredList<Product> products, String text) {
        products.setPredicate(prod -> prod.getLabel().toLowerCase().contains(text)
                || prod.getNumber().toLowerCase().contains(text)
                || prod.getSerialNumber().toLowerCase().contains(text)
                || String.valueOf(prod.getConvertedUnitPrice()).contains(text)
                || String.valueOf(prod.getStockQuantity()).contains(text)
                || prod.getProvider().getLabel().toLowerCase().contains(text)
                || prod.getCategory().getLabel().toLowerCase().contains(text));
    }

    private void setExcelEventHandler() {
        excel.setOnAction(e -> Platform.runLater(() -> {
            List<Product> items = productsTable.getSelectionModel().getSelectedItems();
            if (!items.isEmpty()) ProductExporter.export(items, ProductExporter.UnitPriceContext.WITHOUT_TAX);
        }));
    }
}

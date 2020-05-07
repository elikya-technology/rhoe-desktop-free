/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.chart.ProductsCharter;
import com.elikya.apps.rhoe.desk.chart.ProductsCharter.ChartContext;
import com.elikya.apps.rhoe.desk.exporters.ProductExporter;
import com.elikya.apps.rhoe.desk.entity.Category;
import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.service.ProductService;
import com.elikya.apps.rhoe.desk.service.SaleLineService;
import com.elikya.apps.rhoe.desk.observers.impl.*;
import com.elikya.apps.rhoe.desk.observers.interfaces.*;
import com.elikya.apps.rhoe.desk.ui.*;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import com.elikya.apps.rhoe.desk.util.LicenseListener;
import com.elikya.apps.rhoe.desk.util.NumbersFormatter;
import com.elikya.apps.rhoe.desk.util.TableViewOperation;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class ProductsController implements Initializable, LanguageObserver
        , CurrencyObserver, DecimalsObserver, ValidationObserver, SaveUpdateObserver {

    @FXML private CustomTextField searchText;
    @FXML private TableColumn<Product, String> name;
    @FXML private TableColumn<Product, Category> category;
    @FXML private JFXButton more;
    @FXML private MenuItem delete;
    @FXML private MenuItem excel;
    @FXML private TableColumn<Product, Integer> realQuantity;
    @FXML private TableColumn<Product, String> modelNumber;
    @FXML private TableColumn<Product, String> productNumber;
    @FXML private TableColumn<Product, BigDecimal> taxedPrice;
    @FXML private TableView<Product> productsTable;
    @FXML private MenuItem _stockDetails;
    @FXML private MenuItem _edit;
    @FXML private MenuItem balance;
    @FXML private JFXButton handle;
    @FXML private JFXComboBox<String> chartBy;
    @FXML private Label labelCats;
    @FXML private Label labelProds;
    @FXML private VBox catsBox;
    @FXML private VBox prodsBox;
    @FXML private JFXButton searchBtn;
    @FXML private MenuItem _stockUp;
    @FXML private MenuItem _withdraw;
    
    private ProductService productService;
    private SaleLineService saleLineService;
    
    private Properties lang;
    private BarChart<String, Number> productsChart;
    private List<Product> productsList;
    private ObservableList<Product> deletableItems;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (LicenseListener.licenseIsValid()) {
            LanguageObserverImpl.register(this);
            CurrencyObserverImpl.register(this);
            DecimalsObserverImpl.register(this);
            ValidationObserverImpl.register(this);
            SaveUpdateObserverImpl.register(this);
            ControlsHandler.handleSearchZone(searchText, searchBtn);
            queryProducts();
            TableViewOperation.setTableSelectionModel(productsTable);
            TableViewOperation.handleSelection(productsTable);
            updateLanguage();
            setTableCellsValueFactory();
            intiMethods();
            setTaxedPriceCellFactory();
        }
    }
    
    @Autowired
    private void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    private void setSaleLineService(SaleLineService saleLineService) {
        this.saleLineService = saleLineService;
    }

    private void queryProducts() {
        productsList = productService.getAll();
        putProductsInTable();
    }

    public void putProductsInTable() {
        FilteredList<Product> items = filterProducts();
        SortedList<Product> sortedItems = sortProducts(items);
        productsTable.setItems(sortedItems);
        productsTable.refresh();
        setSearchTextProperty(items);
    }

    private SortedList<Product> sortProducts(FilteredList<Product> items) {
        SortedList<Product> sortedItems = new SortedList<>(items);
        sortedItems.comparatorProperty().bind(productsTable.comparatorProperty());
        return sortedItems;
    }

    private void intiMethods() {
        setChartByItems();
        chartCategories();
        chartProducts(productsTable.getItems(), ChartState.DISABLED);
        setAddEventHandler();
        setMoreEventHandler();
        setDeleteEventHandler();
        setEditEventHandler();
        setChartByEventHandler();
        setDetailsEventHandler();
        setProductsTableClickHandler();
        setExcelEventHandler();
        setStockUpEventHandler();
        setBalanceEventHandler();
        setWithdrawEventHandler();
    }

    @Override
    public void updateLanguage() {
        setControlsBundle();
        setControlsTooltips();
    }

    @Override
    public void updateCurrency() {
        setCurrencySymbol();
        queryProducts();
        chartCategories();
        chartProducts(productsTable.getItems(), ChartState.DISABLED);
    }

    @Override
    public void updateDecimals() {
        putProductsInTable();
    }

    @Override
    public void processUpdateValidation() {
        Product item = productsTable.getSelectionModel().getSelectedItem();
        ProductHandlerController.setProductId(item.getId());
        Stages.showResponsiveDialog(StagesPaths.PRODUCT_HANDLER, StageSize.MEDIUM);
    }

    @Override
    public void processStockingUp() {
        Product selected = productsTable.getSelectionModel().getSelectedItem();
        StockUpController.setProduct(selected);
        Stages.showDialog(StagesPaths.STOCK_UP);
    }

    @Override
    public void processWithdraw() {
        Product selected  = productsTable.getSelectionModel().getSelectedItem();
        WithdrawController.setProduct(selected);
        Stages.showDialog(StagesPaths.WITHDRAW);
    }

    @Override
    public void processDeletionValidation() {
        Platform.runLater(() -> {
            deletableItems = productsTable.getSelectionModel().getSelectedItems();
            productService.deleteAll(deletableItems);
            productsList.removeAll(deletableItems);
            putProductsInTable();
            chartCategories();
            chartProducts(productsTable.getItems(), ChartState.DISABLED);
        });
    }

    @Override
    public void addRecord() {
        productsList.add(productService.getLast());
        putProductsInTable();
        chartCategories();
        chartProducts(productsTable.getItems(), ChartState.DISABLED);
    }

    @Override
    public void updateRecord() {
        queryProducts();
        chartCategories();
        chartProducts(productsTable.getItems(), ChartState.DISABLED);
    }

    private void setControlsBundle() {
        lang = ControlsHandler.getLanguage();
        chartBy.setPromptText(lang.getProperty("compute_by"));
        searchText.setPromptText(lang.getProperty("search"));
        name.setText(lang.getProperty("name"));
        category.setText(lang.getProperty("category"));
        realQuantity.setText(lang.getProperty("quantity"));
        productsTable.setPlaceholder(new Label(lang.getProperty("table_prompt")));
        excel.setText(lang.getProperty("export"));
        _edit.setText(lang.getProperty("edit"));
        delete.setText(lang.getProperty("delete"));
        _stockDetails.setText(lang.getProperty("resume"));
        modelNumber.setText(lang.getProperty("serial_number"));
        productNumber.setText(lang.getProperty("product_number"));
        labelCats.setText(lang.getProperty("categories"));
        labelProds.setText(lang.getProperty("products"));
        _stockUp.setText(lang.getProperty("stock_up"));
        _withdraw.setText(lang.getProperty("withdraw"));
        balance.setText(lang.getProperty("moves"));
        setCurrencySymbol();
    }

    private void setCurrencySymbol() {
        String currency = ApplicationCurrency.getActualCurrency();
        taxedPrice.setText(lang.getProperty("unit_price_tax") + " (" + currency + ")");
    }

    private void setAddEventHandler() {
        handle.setOnAction(event -> Stages.showDialog(StagesPaths.PRODUCT_HANDLER));
    }

    private void setStockUpEventHandler() {
        _stockUp.setOnAction(event -> {
            CodeVerifierController.setContext(CodeVerifierController.VerificationContext.STOCK_UP);
            Stages.showDialog(StagesPaths.CODE_VERIFIER);
        });
    }

    private void setWithdrawEventHandler() {
        _withdraw.setOnAction(event -> {
            CodeVerifierController.setContext(CodeVerifierController.VerificationContext.WITHDRAW);
            Stages.showDialog(StagesPaths.CODE_VERIFIER);
        });
    }
    
    private FilteredList<Product> filterProducts() {
        return new FilteredList<>(FXCollections.observableArrayList(productsList), data -> true);
    }

    private void setControlsTooltips() {
        handle.setTooltip(ControlsHandler.createTooltip("#FF6F00", lang.getProperty("new_product")));
        more.setTooltip(ControlsHandler.createTooltip("#00BCAD", lang.getProperty("more")));
    }
    
    private void setChartByItems() {
        chartBy.setItems(FXCollections.observableArrayList(lang
                .getProperty("price"), lang.getProperty("quantity")));
        chartBy.getSelectionModel().selectFirst();
    }

    private void setMoreEventHandler() {
        more.setOnAction(event -> Stages.showResponsiveDialog(StagesPaths.MORE, StageSize.LARGER));
    }
    
    private void setDeleteEventHandler() {
        delete.setOnAction(e -> {
            deletableItems = productsTable.getSelectionModel().getSelectedItems();
            boolean anyProductIsSold = saleLineService.anyProductIsSold(deletableItems);
            if (anyProductIsSold) {
                Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("delete_sales_first"));
            } else {
                Stages.showDialog(StagesPaths.DELETION_DIALOG);
            }
        });
    }
    
    private void setEditEventHandler() {
        _edit.setOnAction(event -> {
            CodeVerifierController.setContext(CodeVerifierController.VerificationContext.UPDATING);
            Stages.showDialog(StagesPaths.CODE_VERIFIER);
        });
    }
    
    private void setChartByEventHandler() {
        chartBy.setOnAction(e -> {
            productsTable.getSelectionModel().clearSelection();
            chartCategories();
            chartProducts(productsTable.getItems(), ChartState.DISABLED);
        });
    }
    
    private void setDetailsEventHandler() {
        _stockDetails.setOnAction(e -> showProductDetails());
    }

    private void setBalanceEventHandler() {
        balance.setOnAction(event -> {
            List<Integer> ids = getSelectedItemsIds();
            LogsResumeController.setIds(ids);
            Stages.showResponsiveDialog(StagesPaths.LOGS_RESUME, StageSize.LARGER);
        });
    }

    private List<Integer> getSelectedItemsIds() {
        List<Product> products = productsTable.getSelectionModel().getSelectedItems();
        return products.stream().map(Product::getId).distinct().collect(Collectors.toList());
    }

    private void setExcelEventHandler() {
        excel.setOnAction(e -> Platform.runLater(() -> {
            List<Product> items = productsTable.getSelectionModel().getSelectedItems();
            if (!items.isEmpty())
                ProductExporter.export(items, ProductExporter.UnitPriceContext.WITH_TAX);
        }));
    }

    private void showProductDetails() {
        Optional<Product> item = Optional.ofNullable(
                productsTable.getSelectionModel().getSelectedItem());
        item.ifPresent(it -> {
            ProductStockDetailsController.setProduct(it);
            Stages.showResponsiveDialog(StagesPaths.PRODUCT_LOGS, StageSize.LARGER);
        });
    }
    
    private void setSearchTextProperty(FilteredList<Product> products) {
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
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
        Platform.runLater(() -> products.setPredicate(prod -> prod.getBarCode().contains(text)
                || prod.getCategory().getLabel().toLowerCase().contains(text)
                || prod.getLabel().toLowerCase().contains(text)
                || prod.getNumber().toLowerCase().contains(text)
                || prod.getProvider().getLabel().toLowerCase().contains(text)
                || prod.getSerialNumber().toLowerCase().contains(text)
                || String.valueOf(prod.getStockQuantity()).contains(text)
                || String.valueOf(prod.getUnitPrice()).contains(text)));
    }
    
    private void setTableCellsValueFactory() {
        name.setCellValueFactory(new PropertyValueFactory<>("label"));
        modelNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        taxedPrice.setCellValueFactory(new PropertyValueFactory<>("convertedUnitPriceTax"));
        realQuantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
        productNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
    }

    private void setTaxedPriceCellFactory() {
        taxedPrice.setCellFactory(param -> new TableCell<Product, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty) {
                    setText(NumbersFormatter.getFormattedString(item));
                }
            }
        });
    }
    
    private void chartCategories() {
        String chartingContext = chartBy.getSelectionModel().getSelectedItem();
        BarChart<String, Number> categoriesChart = chartingContext.equals(lang.getProperty("quantity"))
                ? ProductsCharter.chartProducts(ChartContext.CATEGORY_QUANTITY, productsTable.getItems()) 
                : ProductsCharter.chartProducts(ChartContext.CATEGORY_PRICE, productsTable.getItems());
        catsBox.getChildren().clear();
        catsBox.getChildren().add(categoriesChart);
        setChartEventHandler(categoriesChart, ChartState.ENABLED);
    }
    
    private void chartProducts(ObservableList<Product> products, ChartState context)  {
        String chartingContext = chartBy.getSelectionModel().getSelectedItem();
        productsChart = chartingContext.equals(lang.getProperty("quantity"))
                ? ProductsCharter.chartProducts(ChartContext.PRODUCTS_QUANTITIES, products) 
                : ProductsCharter.chartProducts(ChartContext.PRODUCTS_PRICE, products);
        prodsBox.getChildren().clear();
        prodsBox.getChildren().add(productsChart);
        setProductsChartEventHandler(context);
        setChartEventHandler(productsChart, ChartState.DISABLED);
    }
    
    private void setChartEventHandler(BarChart<String, Number> chart, ChartState context) {
        chart.getData().forEach(data -> data.getData().forEach(item -> {
            Node node = item.getNode();
            node.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    productsTable.getSelectionModel().clearSelection();
                    if (context.equals(ChartState.ENABLED)) {
                        productsTable.getItems().stream().filter(product -> product.getCategory()
                                .getId().equals(item .getExtraValue()))
                                .forEach(productsTable.getSelectionModel()::select);
                        chartProducts(productsTable.getSelectionModel().getSelectedItems(),  context);
                    } else {
                        productsTable.getItems().stream().filter(product -> product.getId()
                                .equals(item .getExtraValue()))
                                .forEach(productsTable.getSelectionModel()::select);
                    }
                }
            });
        }));
    }
        
    private void setProductsChartEventHandler(ChartState context) {
        ContextMenu contextMenu = new ContextMenu();
        productsChart.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem item = new MenuItem(lang.getProperty("previous"));
                item.setOnAction(itemEvent -> {
                    productsTable.getSelectionModel().clearSelection();
                    chartProducts(productsTable.getItems(), ChartState.DISABLED);
                });
                if (context.equals(ChartState.ENABLED)) item.setDisable(false);
                else item.setDisable(true);
                contextMenu.getItems().setAll(item);
                contextMenu.setHideOnEscape(true);
                contextMenu.show(productsChart, event.getScreenX(), event.getScreenY());
            }
        });
        productsChart.setOnMousePressed(event -> contextMenu.hide());
    }
    
    private void setProductsTableClickHandler() {
        productsTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2)
                showProductDetails();
        });
    }
    
    
    private enum ChartState {ENABLED, DISABLED}
    
}

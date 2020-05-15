/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.bill.BillBuilder;
import com.elikya.apps.rhoe.desk.configs.NumbersConfig;
import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.ProductLog;
import com.elikya.apps.rhoe.desk.entity.Sale;
import com.elikya.apps.rhoe.desk.entity.SaleLine;
import com.elikya.apps.rhoe.desk.service.ProductLogService;
import com.elikya.apps.rhoe.desk.service.ProductService;
import com.elikya.apps.rhoe.desk.service.SaleService;
import com.elikya.apps.rhoe.desk.observers.impl.ProductQtyObserverImpl;
import com.elikya.apps.rhoe.desk.observers.impl.SaveUpdateObserverImpl;
import com.elikya.apps.rhoe.desk.observers.impl.ValidationObserverImpl;
import com.elikya.apps.rhoe.desk.observers.interfaces.ProductQtyObserver;
import com.elikya.apps.rhoe.desk.observers.interfaces.ValidationObserver;
import com.elikya.apps.rhoe.desk.ui.*;
import com.elikya.apps.rhoe.desk.util.*;
import com.elikya.apps.rhoe.desk.configs.NumbersConfig.NumberTarget;
import com.elikya.apps.rhoe.desk.util.TableViewOperation.FactoryContext;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class NewSaleController implements Initializable, ProductQtyObserver, ValidationObserver {

    @FXML private JFXButton close;
    @FXML private Label title;
    @FXML private JFXTextField saleNumber;
    @FXML private CustomTextField searchText;
    @FXML private MenuItem select;
    @FXML private TableColumn<Product, String> productNumber;
    @FXML private TableColumn<Product, String> productName;
    @FXML private JFXButton save;
    @FXML private MenuItem delete;
    @FXML private TableView<SaleLine> salesTable;
    @FXML private TableColumn<SaleLine, Product> category;
    @FXML private TableColumn<SaleLine, Product> number;
    @FXML private TableColumn<SaleLine, BigDecimal> price;
    @FXML private TableColumn<SaleLine, Integer> quantity;
    @FXML private TableColumn<SaleLine, Product> serialNumber;
    @FXML private TableColumn<SaleLine, Product> name;
    @FXML private TableColumn<SaleLine, Product> unitPrice;
    @FXML private JFXTextField productsNumber;
    @FXML private JFXTextField articlesNumber;
    @FXML private JFXTextField totalPrice;
    @FXML private JFXTextField taxedPrice;
    @FXML private TableColumn<Product, Integer> productQuantity;
    @FXML private TableView<Product> productsTable;
    @FXML private MenuItem _edit;
    @FXML private Label productsLbl;
    @FXML private JFXButton searchBtn;
    @FXML private TableColumn<Product, BigDecimal> productUnitPrice;
    @FXML private TableColumn<Product, String> productSerial;
    @FXML private CustomTextField barcodeSearch;
    @FXML private JFXButton barcodeBtn;
    @FXML private SplitPane splitPane;
    @FXML private Label fromClientLbl;
    @FXML private JFXTextField receivedFromClient;
    @FXML private JFXTextField restOfOrder;
    
    private ProductService productService;
    private SaleService saleService;
    private ProductLogService productLogService;

    private String currency;
    private String closeValue;
    private SaleLine selectedSaleLine;
    private Properties lang;
    private Sale sale;

    private Properties options;
    private StringBuilder barCodeConvertedKeyCodes = new StringBuilder();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ProductQtyObserverImpl.register(this);
        ValidationObserverImpl.register(this);
        lang = ControlsHandler.getLanguage();
        ControlsHandler.handleSearchZone(searchText, searchBtn);
        SplitPaneDividerManager.resize(splitPane);
        initControls();
        initOptions();
        bundleFirst();
        bundleSecond();
        buildSaleNumber();
        createNewSale();
        setProductsInTable();
        setSalesTableItemsHandler();
        setCloseEventHandler();
        setSelectEventHandler();
        setDeleteEventHandler();
        setEditEventHandler();
        setProductsTableClickHandler();
        setReceivedFromClientTextProperty();
        setSalesTableClickHandler();
        setSaveEvenHandler();
        setBarcodeBtnEventHandler();
        setBarcodeSearchEventHandler();
        setProductUnitPriceCellFactory();
        setPriceCellFactory();
    }

    private void createNewSale() {
        sale = new Sale();
    }

    @Autowired
    private void setProductService(ProductService productService) {
        this.productService = productService;
    }
    
    @Autowired
    private void setSaleService(SaleService saleService) {
        this.saleService = saleService;
    }

    @Autowired
    private void setProductLogService(ProductLogService productLogService) {
        this.productLogService = productLogService;
    }

    @Override
    public void processUpdateValidation() {
        List<SaleLine> items = salesTable.getSelectionModel().getSelectedItems();
        salesTable.getItems().removeAll(items);
        salesTable.refresh();
    }

    private void initOptions() throws NumberFormatException {
        options = RhoeConfig.get();
        currency = ApplicationCurrency.getActualCurrency();
        closeValue = options.getProperty("close_sale");
    }

    public void initControls() {
        TableViewOperation.handleSelection(productsTable);
        TableViewOperation.setTableSelectionModel(productsTable);
        TableViewOperation.handleSelection(salesTable);
        TableViewOperation.setTableSelectionModel(salesTable);
        TableViewOperation.setSaleProductsTableCellFactory(FactoryContext.NAME, name);
        TableViewOperation.setSaleProductsTableCellFactory(FactoryContext.CATEGORY, category);
        TableViewOperation.setSaleProductsTableCellFactory(FactoryContext.PRODUCT_NUMBER, number);
        TableViewOperation.setSaleProductsTableCellFactory(FactoryContext.SERIAL_NUMBER, serialNumber);
        TableViewOperation.setSaleProductsTableCellFactory(FactoryContext.UNIT_PRICE, unitPrice);
        ControlsHandler.keepFloatValues(receivedFromClient);
        setProductsTableCellValueFactory();
        setSalesTableCellValueFactory();
    }

    @Override
    public void updateQty(int quantity) {
        if (selectedSaleLine != null) {
            processSelectedProduct(selectedSaleLine.getProduct(), quantity);
            barcodeSearch.requestFocus();
        }
    }

    private void setCloseEventHandler() {
        close.setOnAction(event -> {
            ProductQtyObserverImpl.unregister();
            ValidationObserverImpl.unregister(this);
            Stages.close(event);
        });
    }

    public void bundleSecond() {
        salesTable.setPlaceholder(new Label(lang.getProperty("table_prompt")));
        _edit.setText(lang.getProperty("edit"));
        delete.setText(lang.getProperty("remove"));
        select.setText(lang.getProperty("select"));
        serialNumber.setText(lang.getProperty("serial_number"));
        productsNumber.setPromptText(lang.getProperty("products_number"));
        articlesNumber.setPromptText(lang.getProperty("products_quantity"));
        totalPrice.setPromptText(lang.getProperty("total_price") + " (" + currency + ")");
        taxedPrice.setPromptText(lang.getProperty("total_price_tax") + " (" + currency + ")");
        saleNumber.setPromptText(lang.getProperty("sale_number"));
        productsTable.setPlaceholder(new Label(lang.getProperty("table_prompt")));
        productsLbl.setText(lang.getProperty("products"));
        fromClientLbl.setText(lang.getProperty("from_client"));
        receivedFromClient.setPromptText(lang.getProperty("money_received") + " (" + currency + ")");
        restOfOrder.setPromptText(lang.getProperty("rest") + " (" + currency + ")");
    }

    public void bundleFirst() {
        title.setText(lang.getProperty("new_sale"));
        save.setText(lang.getProperty("save"));
        name.setText(lang.getProperty("name"));
        productName.setText(lang.getProperty("name"));
        String unitCurrency = lang.getProperty("unit_price_tax") + " (" + currency + ")";
        productUnitPrice.setText(unitCurrency);
        productSerial.setText(lang.getProperty("serial_number"));
        number.setText(lang.getProperty("product_number"));
        productNumber.setText(lang.getProperty("product_number"));
        productQuantity.setText(lang.getProperty("quantity"));
        category.setText(lang.getProperty("category"));
        quantity.setText(lang.getProperty("quantity"));
        price.setText(lang.getProperty("price") + " (" + currency + ")");
        unitPrice.setText(unitCurrency);
        searchText.setPromptText(lang.getProperty("search"));
        barcodeSearch.setPromptText(lang.getProperty("scan_barcode"));
    }

    private void setProductsTableCellValueFactory() {
        productNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        productName.setCellValueFactory(new PropertyValueFactory<>("label"));
        productQuantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        productSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        productUnitPrice.setCellValueFactory(new PropertyValueFactory<>("convertedUnitPriceTax"));
    }

    private void setSalesTableCellValueFactory() {
        name.setCellValueFactory(new PropertyValueFactory<>("product"));
        serialNumber.setCellValueFactory(new PropertyValueFactory<>("product"));
        number.setCellValueFactory(new PropertyValueFactory<>("product"));
        category.setCellValueFactory(new PropertyValueFactory<>("product"));
        unitPrice.setCellValueFactory(new PropertyValueFactory<>("product"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void setProductUnitPriceCellFactory() {
        productUnitPrice.setCellFactory(param -> new TableCell<Product, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty)
                    setText(NumbersFormatter.getFormattedString(item));
            }
        });
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

    private void buildSaleNumber() {
        String num = NumbersConfig.pickNumber(NumberTarget.SALE);
        String numberValue = LocalDate.now().format(
                DateTimeFormatter.BASIC_ISO_DATE) + "-" + num;
        saleNumber.setText(numberValue);
    }

    private void setProductsInTable() {
        List<Product> products = productService.getAll();
        FilteredList<Product> filteredProducts = filterProducts(products);
        SortedList<Product> sortedItems = sortProducts(filteredProducts);
        productsTable.setItems(sortedItems);
        productsTable.refresh();
        setSearchTextProperty(filteredProducts);
    }

    private FilteredList<Product> filterProducts(List<Product> products) {
        return new FilteredList<>(FXCollections.observableArrayList(products), data -> true);
    }

    private SortedList<Product> sortProducts(FilteredList<Product> filteredProducts) {
        SortedList<Product> sortedItems = new SortedList<>(filteredProducts);
        sortedItems.comparatorProperty().bind(productsTable.comparatorProperty());
        return sortedItems;
    }

    private void setSelectEventHandler() {select.setOnAction(e -> {
        ObservableList<Product> selected = productsTable
                .getSelectionModel().getSelectedItems();
        if (!selected.isEmpty()) pickSelectedProducts(selected);
    });}

    private void pickSelectedProducts(ObservableList<Product> selected) {
        Platform.runLater(() -> {
            List<SaleLine> saleLines = new ArrayList<>(1);
            selected.forEach(item -> {
                if (item.getStockQuantity() > pickProductLimitQty(item)) {
                    if (productAlreadySelected(item)) processSelectedProduct(item, 0);
                    else {
                        saleLines.add(new SaleLine(item, sale, 1, item.getConvertedUnitPriceTax()));
                        updateProductQuantity(0, item, 0);
                        productsTable.refresh();
                    }
                } else Notifier.notify(StagesPaths.WARNING_NOTIF
                        , lang.getProperty("min_qty_alert") + " (" + item.getNumber() + ")");
            });
            salesTable.getItems().addAll(saleLines);
            setPricesFieldsValues();
            computeProductsNumbers();
            productsTable.getSelectionModel().clearSelection();
            barcodeSearch.requestFocus();
        });
    }

    private boolean productAlreadySelected(Product p) {
        Optional<SaleLine> item = getSelectedSaleLine(p);
        return item.isPresent();
    }

    private void processSelectedProduct(Product product, int value) {
        Optional<SaleLine> item = getSelectedSaleLine(product);
        item.ifPresent(it -> {
            updateProductQuantity(value, product, it.getQuantity());
            updateSaleLineProductQuantity(value, it);
            computeProductsNumbers();
            setPricesFieldsValues();
            productsTable.refresh();
        });
    }

    private Optional<SaleLine> getSelectedSaleLine(Product product) {
        return salesTable.getItems().stream().filter(p
                -> p.getProduct().getId().equals(product.getId())).findAny();
    }

    private void updateSaleLineProductQuantity(int value, SaleLine it) {
        it.incrementQuantity(getProductNewQuantity(value, it.getQuantity()));
        it.setPrice(getUpdatedProductPrice(value, it));
        salesTable.refresh();
    }

    private int getProductNewQuantity(int value, int oldQuantity) {
        return value != 0 ? value - oldQuantity : 1;
    }

    private BigDecimal getUpdatedProductPrice(int value, SaleLine i) {
        return value != 0 ? i.getProduct().getConvertedUnitPriceTax().multiply(BigDecimal.valueOf(value))
                : i.getProduct().getConvertedUnitPriceTax().multiply(BigDecimal.valueOf(i.getQuantity()));
    }

    private void updateProductQuantity(int itemsToRemove, Product it, int oldSoldQuantity) {
        Product product = getSelectedProduct(it);
        boolean valueDifferentOfZero = itemsToRemove != 0;
        if (valueDifferentOfZero) product.increaseQuantity(oldSoldQuantity);
        product.decreaseQuantity(valueDifferentOfZero ? itemsToRemove : 1);
    }

    private int pickProductLimitQty(Product product) {
        String min = options.getProperty("min_on_sale");
        return min.equals("1") ? Integer.parseInt(min) : product.getMinimumQuantity();
    }

    private void setDeleteEventHandler() {
        delete.setOnAction(e -> {
            CodeVerifierController.setContext(
                    CodeVerifierController.VerificationContext.UPDATING);
            Stages.showDialog(StagesPaths.CODE_VERIFIER);
        });
    }

    private void setReceivedFromClientTextProperty() {
        receivedFromClient.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                restOfOrder.clear();
                save.setDisable(true);
            } else {
                try {
                    BigDecimal rest = computeRest(BigDecimal.valueOf(Double.parseDouble(newValue)));
                    restOfOrder.setText(NumbersFormatter.getFormattedString(rest));
                    save.setDisable(false);
                } catch (NumberFormatException exception) {
                    save.setDisable(true);
                    exception.printStackTrace();
                }
            }
        });
    }

    private BigDecimal computeRest(BigDecimal value) {
        String unformattedTaxedPrice = NumbersFormatter.removeThousandsSeparator(taxedPrice.getText());
        BigDecimal doubleTaxedPrice = BigDecimal.valueOf(Double.parseDouble(unformattedTaxedPrice));
        return value.subtract(doubleTaxedPrice);
    }

    private void setSalesTableItemsHandler() {
        salesTable.getItems().addListener((ListChangeListener.Change<? extends SaleLine> c) -> {
            if (c.next()) {
                if (c.wasRemoved()) {
                    increaseProductsQuantities(c);
                }
                setPricesFieldsValues();
                computeProductsNumbers();
                productsTable.refresh();
            }
        });
    }

    private void increaseProductsQuantities(ListChangeListener.Change<? extends SaleLine> c) {
        c.getRemoved().forEach(i -> {
            Product product = getSelectedProduct(i.getProduct());
            product.increaseQuantity(i.getQuantity());
        });
    }

    private void setProductsTableClickHandler() {
        productsTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                Optional<Product> selected = Optional.ofNullable(productsTable
                        .getSelectionModel().getSelectedItem());
                selected.ifPresent(it -> pickSelectedProducts(FXCollections.observableArrayList(it)));
            }
        });
    }

    private void setSalesTableClickHandler() {
        salesTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2)
                if (!_edit.isDisable()) pickEditableQuantityItem();
        });
    }

    private void setEditEventHandler() {
        _edit.setOnAction(e -> pickEditableQuantityItem());
    }

    private void setSaveEvenHandler() {
        save.setOnAction(e -> Platform.runLater(() -> {
            if (Double.parseDouble(NumbersFormatter.removeThousandsSeparator(restOfOrder.getText())) < 0)
                Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("insufficient_amount"));
            else {
                setOrderPrices();
                addSale();
                updateSoldProductsQty();
                handleStageEnclosing(e);
            }
        }));
    }

    private void updateSoldProductsQty() {
        List<Product> soldProducts = productsTable.getItems().stream()
                .filter(it -> salesTable.getItems().stream().anyMatch(i
                        -> i.getProduct().getId().equals(it.getId())))
                .collect(Collectors.toList());
        productService.updateProductsQty(soldProducts);
        productsTable.refresh();
    }

    private void setOrderPrices() {
        try {
            sale.setTaxedPrice(computeUnconvertedTotalPriceTax());
            sale.setTotalPrice(computeUnconvertedTotalPrice());
            sale.setMoneyReceived(new BigDecimal(NumbersFormatter
                    .removeThousandsSeparator(receivedFromClient.getText())));
        } catch (NumberFormatException exception) {
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_numeric_values"));
        }
    }

    private BigDecimal computeUnconvertedTotalPriceTax() {
        return salesTable.getItems().stream().map(it -> it.getProduct().getUnitPriceTax()
                .multiply(BigDecimal.valueOf(it.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void setSearchTextProperty(FilteredList<Product> products) {
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                products.setPredicate(p -> true);
                barcodeSearch.setDisable(false);
            }
            else {
                barcodeSearch.setDisable(true);
                String text = newValue.toLowerCase();
                searchProduct(products, text);
                productsTable.refresh();
            }
        });
    }


    private void setBarcodeSearchEventHandler() {
        barcodeSearch.setOnKeyReleased(e -> {
            if (e.getText().trim().isEmpty())
                searchText.setDisable(false);
            else {
                barCodeConvertedKeyCodes.append(KeyCodeText.getAdaptedText(e));
                barcodeSearch.setText(barCodeConvertedKeyCodes.toString());
                searchText.setDisable(true);
            }
            if (textIsDeleted(e)) {
                resetResearchControls();
            }
        });
    }

    private void resetResearchControls() {
        barcodeSearch.clear();
        searchText.setDisable(false);
        barCodeConvertedKeyCodes.setLength(0);
    }

    private boolean textIsDeleted(KeyEvent event) {
        return event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE;
    }

    private void setBarcodeBtnEventHandler() {
        barcodeBtn.setOnAction(e -> {
            if (!barcodeSearch.getText().isEmpty()) {
                Optional<Product> product = searchByBarcode(barcodeSearch.getText());
                if (product.isPresent())
                    pickSelectedProducts(FXCollections.observableArrayList(product.get()));
                else
                    Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("no_product"));
                barcodeSearch.clear();
                barCodeConvertedKeyCodes.setLength(0);
            }
        });
    }

    private BigDecimal computeUnconvertedTotalPrice() {
        return salesTable.getItems().stream().map(it -> it.getProduct().getUnitPrice()
                .multiply(BigDecimal.valueOf(it.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void addSale() {
        try {
            setNewSaleDetails();
            saleService.save(sale);
            logSoldProducts();
            NumbersConfig.incrementNumber(NumberTarget.SALE);
            SaveUpdateObserverImpl.executeAddRecord();
            BillBuilder.buildAndPrint(sale);
            Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty("sale_added"));
        } catch (DataIntegrityViolationException exception) {
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("sale_not_added"));
        }
    }

    private void setNewSaleDetails() {
        sale.setNumber(saleNumber.getText());
        sale.setSaleDate(LocalDate.now());
        sale.setSaleTime(LocalTime.now());
        sale.setRate(ApplicationCurrency.getActualRate());
        sale.setCurrency(ApplicationCurrency.getActualCurrency());
        sale.setLines(salesTable.getItems());
    }

    private Optional<Product> searchByBarcode(String text) {
        return productsTable.getItems().stream().filter(prod
                -> prod.getBarCode().equals(text)).findAny();
    }

    private void logSoldProducts() {
        List<ProductLog> logs = sale.getLines().stream()
                .map(this::buildProductLog).collect(Collectors.toList());
        productLogService.saveAll(logs);
    }

    private ProductLog buildProductLog(SaleLine i) {
        Product product = i.getProduct();
        return ProductLog.builder().actionQty(i.getQuantity())
                .logAction(ProductLoggingAction.ADDING.text)
                .logDate(sale.getSaleDate())
                .logTime(sale.getSaleTime())
                .product(product).stockQty(getProductUpdatedQty(product))
                .unitPrice(product.getUnitPriceTax())
                .actualCurrency(ApplicationCurrency.getActualCurrency())
                .currencyRate(ApplicationCurrency.getActualRate())
                .reason("")
                .build();
    }

    private int getProductUpdatedQty(Product product) {
        Optional<Product> item = productsTable.getItems().stream().filter(p
                -> p.getId().equals(product.getId())).findFirst();
        return item.map(Product::getStockQuantity).orElse(0);
    }

    public void handleStageEnclosing(ActionEvent e) {
        if (Boolean.parseBoolean(closeValue)) {
            Stages.close(e);
        } else {
            resetOrder();
        }
    }

    private void resetOrder() {
        refreshSalesTable();
        restFields();
        createNewSale();
        buildSaleNumber();
        setProductsInTable();
        productsTable.refresh();
        barcodeSearch.requestFocus();
    }

    private void refreshSalesTable() {
        salesTable.getItems().clear();
        salesTable.refresh();
    }

    private void restFields() {
        productsNumber.clear();
        articlesNumber.clear();
        totalPrice.clear();
        taxedPrice.clear();
        receivedFromClient.clear();
        restOfOrder.clear();
        save.setDisable(true);
    }

    private void pickEditableQuantityItem() {
        Optional<SaleLine> item = Optional.ofNullable(salesTable.getSelectionModel().getSelectedItem());
        item.ifPresent(it -> {
            selectedSaleLine = it;
            ProductQuantityController.setProduct(it);
            Stages.showDialog(StagesPaths.PRODUCT_QUANTITY);
        });
    }

    private void computeProductsNumbers() {
        productsNumber.setText(String.valueOf(salesTable.getItems().size()));
        int qty = salesTable.getItems().stream().mapToInt(SaleLine::getQuantity).sum();
        articlesNumber.setText(String.valueOf(qty));
    }

    private void setPricesFieldsValues() {
        totalPrice.setText(NumbersFormatter.getFormattedString(computeConvertedTotalPrice()));
        taxedPrice.setText(NumbersFormatter.getFormattedString(computeConvertedTotalPriceTax()));
    }

    private BigDecimal computeConvertedTotalPrice() {
        return salesTable.getItems().stream().map(i -> i.getProduct().getConvertedUnitPrice()
                .multiply(BigDecimal.valueOf(i.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal computeConvertedTotalPriceTax() {
        return salesTable.getItems().stream().map(i -> i.getProduct().getConvertedUnitPriceTax()
                .multiply(BigDecimal.valueOf(i.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void searchProduct(FilteredList<Product> products, String text) {
        Platform.runLater(() -> products.setPredicate(prod ->
                prod.getLabel().toLowerCase().contains(text)
                || prod.getNumber().toLowerCase().contains(text)
                || prod.getSerialNumber().toLowerCase().contains(text)));
    }

    private Product getSelectedProduct(Product i) {
        Optional<Product> product = productsTable.getItems().stream()
                .filter(p -> p.getId().equals(i.getId())).findAny();
        return product.orElseGet(Product::new);
    }

    private enum ProductLoggingAction {
        ADDING("sale_created");
        private String text;
        ProductLoggingAction(String text) {
            this.text = text;
        }
    }
}

/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.entity.Category;
import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.ProductLog;
import com.elikya.apps.rhoe.desk.entity.Provider;
import com.elikya.apps.rhoe.desk.service.CategoryService;
import com.elikya.apps.rhoe.desk.service.ProductLogService;
import com.elikya.apps.rhoe.desk.service.ProductService;
import com.elikya.apps.rhoe.desk.service.ProviderService;
import com.elikya.apps.rhoe.desk.observers.impl.CRUDMasterImpl;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import com.elikya.apps.rhoe.desk.configs.NumbersConfig;
import com.elikya.apps.rhoe.desk.configs.NumbersConfig.NumberTarget;
import com.elikya.apps.rhoe.desk.util.KeyCodeText;
import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class ProductHandlerController implements Initializable {

    @FXML private JFXButton close;
    @FXML private JFXButton save;
    @FXML private Label descriptionLabel;
    @FXML private JFXTextField name;
    @FXML private JFXTextField number;
    @FXML private JFXTextField barCode;
    @FXML private JFXComboBox<Provider> provider;
    @FXML private JFXTextField unitPrice;
    @FXML private Label stockLabel;
    @FXML private JFXTextField maximumQuantity;
    @FXML private JFXTextField minimumQuantity;
    @FXML private Label title;
    @FXML private JFXComboBox<Category> category;
    @FXML private JFXTextField modelNumber;
    @FXML private JFXTextField realQuantity;
    @FXML private ScrollPane scrollPane;

    private ProductService productService;
    private CategoryService categoryService;
    private ProviderService providerService;
    private ProductLogService productLogService;

    private Properties lang;
    private Product product;
    private static Integer productId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setCloseListener();
        setLanguage();
        handleNumericFields();
        setProviders();
        setCategories();
        setUpdatableProduct();
        setOldProductValues();
        setSaveEventHandler();
        setBarCodeEventHandler();
        setNameTextProperty();
    }

    @Autowired
    private void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }

    @Autowired
    private void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    private void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    private void setProductLogService(ProductLogService productLogService) {
        this.productLogService = productLogService;
    }

    public static void setProductId(int id) {
        productId = id;
    }

    private void setUpdatableProduct() {
        if (productId != null) {
            Optional<Product> item = this.productService.getFromId(productId);
            item.ifPresent(it -> product = it);
        }
    }

    private void setCloseListener() { 
        close.setOnAction((event) -> {
            Stages.close(event);
            nullifyProduct();
        });
    }

    private void handleNumericFields() {
        ControlsHandler.keepIntegerValues(realQuantity);
        ControlsHandler.keepIntegerValues(maximumQuantity);
        ControlsHandler.keepIntegerValues(minimumQuantity);
        ControlsHandler.keepFloatValues(unitPrice);
    }

    private void setLanguage() {
        lang = ControlsHandler.getLanguage();
        title.setText(productId != null ? lang.getProperty("edit_product")
                : lang.getProperty("new_product"));
        name.setPromptText(lang.getProperty("name"));
        number.setPromptText(lang.getProperty("product_number"));
        category.setPromptText(lang.getProperty("category"));
        provider.setPromptText(lang.getProperty("provider"));
        barCode.setPromptText(lang.getProperty("barcode"));
        unitPrice.setPromptText(lang.getProperty("unit_price").concat(" ("
                + ApplicationCurrency.getDefaultCurrency() + ")"));
        save.setText(lang.getProperty("save"));
        maximumQuantity.setPromptText(lang.getProperty("maximum_quantity"));
        minimumQuantity.setPromptText(lang.getProperty("minimum_quantity"));
        realQuantity.setPromptText(lang.getProperty("stock_quantity"));
        descriptionLabel.setText(lang.getProperty("description"));
        stockLabel.setText(lang.getProperty("quantities"));
        modelNumber.setPromptText(lang.getProperty("serial_number"));
    }

    private void setBarCodeEventHandler() {
        StringBuilder convertedCodes = new StringBuilder();
        barCode.setOnKeyReleased(event -> {
            if (!event.getText().trim().isEmpty()) {
                convertedCodes.append(KeyCodeText.getAdaptedText(event));
                barCode.clear();
                barCode.setText(convertedCodes.toString());
            }
            if (textIsDeleted(event)){
                barCode.clear();
                convertedCodes.setLength(0);
            }
            if (event.getCode() == KeyCode.ENTER) convertedCodes.setLength(0);
        });
    }

    private boolean textIsDeleted(KeyEvent event) {
        return event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE;
    }

    private void setSaveEventHandler() {
        save.setOnAction(event -> Platform.runLater(() -> {
            if (anyRequiredFieldIsEmpty()) {
                Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("required_field_empty"));
            } else if (Integer.parseInt(minimumQuantity.getText()) < 1) {
                Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_min_qty"));
            } else if (!quantitiesAreValid()) {
                Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_quantities"));
            } else if (product != null) {
                handleUpdatableProduct(event);
            } else {
                choosePersistenceAction();
                handleEnclosing(event);
            }
        }));
    }

    private void handleUpdatableProduct(ActionEvent event) {
        if (productHasNotChanged()) {
            Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("no_update"));
        } else {
            updateProduct();
            Stages.close(event);
        }
    }

    private void setNameTextProperty() {
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) number.clear();
            else {
                setProductNumber(newValue);
            }
        });
    }

    private void setProductNumber(String newValue) {
        String num;
        if (product != null) num = product.getNumber().split("-")[1];
        else num = NumbersConfig.pickNumber(NumberTarget.PRODUCT);
        String chars = NumbersConfig.pickChars(newValue);
        number.setText(chars.concat("-").concat(num));
    }

    private void choosePersistenceAction() {
        if (product != null) updateProduct();
        else addProduct();
    }

    private void nullifyProduct() {
        product = null;
        productId = null;
    }

    private void updateProduct() {
        Product.update(product, name.getText(), number.getText(), modelNumber.getText(), barCode.getText()
                , provider.getSelectionModel().getSelectedItem(), category.getSelectionModel().getSelectedItem()
                , Integer.parseInt(maximumQuantity.getText()), Integer.parseInt(minimumQuantity.getText())
                , Integer.parseInt(realQuantity.getText()), BigDecimal.valueOf(Double.parseDouble(unitPrice.getText())));
        productService.update(product);
        CRUDMasterImpl.executeUpdateRecord();
        notifySuccess("product_edited");
    }

    private void addProduct() {
        try {
            product = buildProduct();
            productService.save(product);
            persistAddedProductLog();
            NumbersConfig.incrementNumber(NumberTarget.PRODUCT);
            CRUDMasterImpl.executeAddRecord();
            notifySuccess("product_added");
        } catch (DataIntegrityViolationException e) {
            Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("product_duplicate"));
        }
    }

    private Product buildProduct() {
        return Product.builder().label(name.getText()).number(number.getText())
                .serialNumber(modelNumber.getText()).barCode(barCode.getText())
                .provider(provider.getSelectionModel().getSelectedItem())
                .category(category.getSelectionModel().getSelectedItem())
                .maximumQuantity(Integer.parseInt(maximumQuantity.getText()))
                .minimumQuantity(Integer.parseInt(minimumQuantity.getText()))
                .stockQuantity(Integer.parseInt(realQuantity.getText()))
                .unitPrice(BigDecimal.valueOf(Double.parseDouble(unitPrice.getText()))).build();
    }

    private void notifySuccess(String bundle) {
        Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty(bundle));
    }

    private void handleEnclosing(ActionEvent event) {
        nullifyProduct();
        Properties conf = RhoeConfig.get();
        if (Boolean.parseBoolean(conf.getProperty("close_product"))) Stages.close(event);
        else resetFields();
    }

    private void persistAddedProductLog() {
        ProductLog log = buildProductLog();
        productLogService.save(log);
    }

    private ProductLog buildProductLog() {
        return ProductLog.builder().actionQty(product.getStockQuantity())
                .logAction("prod_created").logDate(LocalDate.now()).logTime(LocalTime.now())
                .product(product).stockQty(Integer.parseInt(realQuantity.getText()))
                .unitPrice(BigDecimal.valueOf(Double.parseDouble(unitPrice.getText())))
                .currencyRate(ApplicationCurrency.getActualRate())
                .actualCurrency(ApplicationCurrency.getActualCurrency())
                .reason("").build();
    }

    private void setProviders() {
        provider.getItems().addAll(providerService.getAll());
        if (product == null) provider.getSelectionModel().selectFirst();
    }

    private void setCategories() {
        category.getItems().addAll(categoryService.getAll());
        if (product == null) category.getSelectionModel().selectFirst();
    }

    private void resetFields() {
        name.clear();
        number.clear();
        modelNumber.clear();
        category.getSelectionModel().selectFirst();
        provider.getSelectionModel().selectFirst();
        unitPrice.clear();
        maximumQuantity.clear();
        minimumQuantity.clear();
        realQuantity.clear();
        barCode.clear();
        name.requestFocus();
        scrollPane.setVvalue(0);
    }

    private void setOldProductValues() {
        if (product != null) {
            name.setText(product.getLabel());
            number.setText(product.getNumber());
            modelNumber.setText(product.getSerialNumber());
            unitPrice.setText(String.valueOf(product.getUnitPrice()));
            maximumQuantity.setText(String.valueOf(product.getMaximumQuantity()));
            minimumQuantity.setText(String.valueOf(product.getMinimumQuantity()));
            realQuantity.setText(String.valueOf(product.getStockQuantity()));
            realQuantity.setDisable(true);
            barCode.setText(product.getBarCode());
            getProductCategory(product);
            getProductProvider(product);
        }
    }
    
    private void getProductCategory(Product p) {
        Optional<Category> item = category.getItems().stream().filter(i -> i.getLabel()
                .equals(p.getCategory().getLabel())).findAny();
        item.ifPresent(value -> category.getSelectionModel().select(value));
    }
    
    private void getProductProvider(Product p) {
        Optional<Provider> item = provider.getItems().stream().filter(i -> i.getLabel()
                .equals(p.getProvider().getLabel())).findAny();
        item.ifPresent(value -> provider.getSelectionModel().select(value));
    }

    private boolean anyRequiredFieldIsEmpty() {
        return name.getText().isEmpty() || modelNumber.getText().isEmpty()
                || maximumQuantity.getText().isEmpty() || minimumQuantity.getText().isEmpty()
                || realQuantity.getText().isEmpty() || unitPrice.getText().isEmpty()
                || category.getSelectionModel().isEmpty()
                || provider.getSelectionModel().isEmpty();
    }
    
    private boolean quantitiesAreValid() {
        int maximum = Integer.parseInt(maximumQuantity.getText());
        int minimum = Integer.parseInt(minimumQuantity.getText());
        int real = Integer.parseInt(realQuantity.getText());
        return (real <= maximum) && (real >= minimum);
    }
    
    private boolean productHasNotChanged() {
        return product.getLabel().equals(name.getText())
                && product.getSerialNumber().toLowerCase().equals(modelNumber.getText().toLowerCase())
                && product.getCategory().equals(category.getSelectionModel().getSelectedItem())
                && product.getProvider().equals(provider.getSelectionModel().getSelectedItem())
                && product.getMaximumQuantity() == Integer.parseInt(maximumQuantity.getText())
                && product.getMinimumQuantity()== Integer.parseInt(minimumQuantity.getText())
                && product.getStockQuantity()== Integer.parseInt(realQuantity.getText())
                && product.getUnitPrice().doubleValue() == Double.parseDouble(unitPrice.getText())
                && product.getBarCode().equals(barCode.getText());
    }
}

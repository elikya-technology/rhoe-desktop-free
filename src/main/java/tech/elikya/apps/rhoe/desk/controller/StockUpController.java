/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.controller;

import tech.elikya.apps.rhoe.desk.entity.Product;
import tech.elikya.apps.rhoe.desk.entity.ProductLog;
import tech.elikya.apps.rhoe.desk.service.ProductLogService;
import tech.elikya.apps.rhoe.desk.service.ProductService;
import tech.elikya.apps.rhoe.desk.observers.impl.CRUDMasterImpl;
import tech.elikya.apps.rhoe.desk.ui.ControlsHandler;
import tech.elikya.apps.rhoe.desk.ui.Notifier;
import tech.elikya.apps.rhoe.desk.ui.Stages;
import tech.elikya.apps.rhoe.desk.ui.StagesPaths;
import tech.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Properties;
import java.util.ResourceBundle;

@Component
public class StockUpController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private JFXTextField quantity;
    @FXML private JFXTextField maxQty;
    @FXML private JFXButton save;
    @FXML private JFXButton cancel;
    @FXML private JFXTextArea reason;

    private static Product product;

    private ProductService productService;
    private ProductLogService productLogService;

    private Properties lang;


    public static void setProduct(Product item) {
        product = item;
    }

    @Autowired
    private void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    private void setProductLogService(ProductLogService productLogService) {
        this.productLogService = productLogService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lang = ControlsHandler.getLanguage();
        ControlsHandler.keepIntegerValues(quantity);
        setLanguage();
        setQuantityTextProperty();
        setCloseEventHandler();
        setCancelEventHandler();
        setSaveEventHandler();
        setMaxQty();
    }

    @SuppressWarnings("DuplicatedCode")
    private void setLanguage() {
        title.setText(lang.getProperty("stock_up"));
        quantity.setPromptText(lang.getProperty("quantity") + " ("
                + product.getLabel() + " " + product.getSerialNumber()+ ")");
        maxQty.setPromptText(lang.getProperty("maximum_quantity"));
        save.setText(lang.getProperty("save"));
        cancel.setText(lang.getProperty("cancel"));
        reason.setPromptText(lang.getProperty("reason")
                + " (" + lang.getProperty("optional") + ")");
    }

    private void setQuantityTextProperty() {
        quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) save.setDisable(true);
            else save.setDisable(false);
        });
    }

    private void setCloseEventHandler() {
        close.setOnAction(this::processEnclosing);
    }

    private void processEnclosing(ActionEvent event) {
        product = null;
        Stages.close(event);
    }

    private void setCancelEventHandler() {
        cancel.setOnAction(this::processEnclosing);
    }

    private void setSaveEventHandler() {
        save.setOnAction(event -> {
            if (newStockIsLessThanMaxQty()) {
                updateProduct();
                logProduct();
                CRUDMasterImpl.executeUpdateRecord();
                Stages.close(event);
            } else
                Notifier.notify(StagesPaths.WARNING_NOTIF, lang
                        .getProperty("max_qty_alert") + " (" + product.getNumber() + ")");
        });
    }

    private boolean newStockIsLessThanMaxQty() throws NumberFormatException {
        int qtyToAdd = getQtyToAdd();
        int newStockQty = product.getStockQuantity() + qtyToAdd;
        return newStockQty <= product.getMaximumQuantity();
    }

    private int getQtyToAdd() {
        try {
            return Integer.parseInt(quantity.getText());
        } catch (NumberFormatException exception) {
            throw new NumberFormatException("INVALID QUANTITY");
        }
    }

    private void updateProduct() {
        int qtyToAdd = getQtyToAdd();
        product.increaseQuantity(qtyToAdd);
        productService.update(product);
    }

    private void logProduct() {
        ProductLog log = buildProductLog();
        productLogService.save(log);
    }

    private ProductLog buildProductLog() {
        return ProductLog.builder().reason(reason.getText())
                .currencyRate(ApplicationCurrency.getActualRate())
                .actualCurrency(ApplicationCurrency.getActualCurrency())
                .actionQty(getQtyToAdd()).logAction("prod_increased")
                .logDate(LocalDate.now()).logTime(LocalTime.now())
                .product(product).stockQty(product.getStockQuantity())
                .unitPrice(product.getUnitPrice()).build();
    }

    private void setMaxQty() {
        maxQty.setText(String.valueOf(product.getMaximumQuantity()));
    }

}

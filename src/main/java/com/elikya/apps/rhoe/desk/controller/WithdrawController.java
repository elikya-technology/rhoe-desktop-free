/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.ProductLog;
import com.elikya.apps.rhoe.desk.service.ProductLogService;
import com.elikya.apps.rhoe.desk.service.ProductService;
import com.elikya.apps.rhoe.desk.observers.impl.CRUDMasterImpl;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
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
public class WithdrawController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private JFXTextField quantity;
    @FXML private JFXTextField minQty;
    @FXML private JFXTextArea reason;
    @FXML private JFXButton save;
    @FXML private JFXButton cancel;

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
        setReasonTextProperty();
        setCloseEventHandler();
        setCancelEventHandler();
        setSaveEventHandler();
        setMinQty();
    }

    @SuppressWarnings("DuplicatedCode")
    private void setLanguage() {
        title.setText(lang.getProperty("withdraw"));
        quantity.setPromptText(lang.getProperty("quantity") + " ("
                + product.getLabel() + " " + product.getSerialNumber()+ ")");
        minQty.setPromptText(lang.getProperty("minimum_quantity"));
        save.setText(lang.getProperty("save"));
        cancel.setText(lang.getProperty("cancel"));
        reason.setPromptText(lang.getProperty("reason"));
    }

    private void setQuantityTextProperty() {
        quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) save.setDisable(true);
            else {
                if (!reason.getText().isEmpty())
                    save.setDisable(false);
            }
        });
    }

    private void setReasonTextProperty() {
        reason.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) save.setDisable(true);
            else {
                if (!quantity.getText().isEmpty())
                    save.setDisable(false);
            }
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
            if (newStockIsGreaterThanMinQty()) {
                updateProduct();
                logProduct();
                CRUDMasterImpl.executeUpdateRecord();
                Stages.close(event);
            } else
                Notifier.notify(StagesPaths.WARNING_NOTIF, lang
                        .getProperty("min_qty_alert") + " (" + product.getNumber() + ")");
        });
    }

    private boolean newStockIsGreaterThanMinQty() {
        int qtyToWithdraw = getQtyToWithdraw();
        int newStockQty = product.getStockQuantity() - qtyToWithdraw;
        return newStockQty >= product.getMinimumQuantity();
    }

    private int getQtyToWithdraw() {
        try {
            return Integer.parseInt(quantity.getText());
        } catch (NumberFormatException exception) {
            throw new NumberFormatException("INVALID QTY");
        }
    }

    private void updateProduct() {
        int qtyToWithdraw = getQtyToWithdraw();
        product.decreaseQuantity(qtyToWithdraw);
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
                .actionQty(getQtyToWithdraw()).logAction("prod_decreased")
                .logDate(LocalDate.now()).logTime(LocalTime.now())
                .product(product).stockQty(product.getStockQuantity())
                .unitPrice(product.getUnitPrice()).build();
    }

    private void setMinQty() {
        minQty.setText(String.valueOf(product.getMinimumQuantity()));
    }

}

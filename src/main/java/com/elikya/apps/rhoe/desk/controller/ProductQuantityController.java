/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.entity.SaleLine;
import com.elikya.apps.rhoe.desk.observers.impl.ProductQtyObserverImpl;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class ProductQuantityController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private JFXTextField quantity;
    @FXML private JFXButton ok;

    private static SaleLine saleLine;
    private Properties lang;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lang = ControlsHandler.getLanguage();
        ControlsHandler.keepIntegerValues(quantity);
        setLanguage();
        setCloseEventHandler();
        setQuantityTextProperty();
        setOkEventHandler();
    }

    public void setLanguage() {
        title.setText(lang.getProperty("sale"));
        quantity.setPromptText(lang.getProperty("quantity") + " ("
                + saleLine.getProduct().getLabel() + " " +
                saleLine.getProduct().getSerialNumber()+ ")");
        ok.setText(lang.getProperty("next"));
    }

    private void setCloseEventHandler() {close.setOnAction(Stages::close);}
    
    public static void setProduct(SaleLine p) {
        saleLine = p;}
    
    private void setQuantityTextProperty() {
        quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) ok.setDisable(true);
            else ok.setDisable(false);
        });
    }
    
    private void setOkEventHandler() {
        ok.setOnAction(e -> {
            int qtyValue = Integer.parseInt(quantity.getText());
            int gapValue = (saleLine.getProduct().getStockQuantity()
                    + saleLine.getQuantity()) - qtyValue;
            if (gapValue >= pickProductLimitQty()) {
                ProductQtyObserverImpl.executeUpdateQty(qtyValue);
                Stages.close(e);
            } else {
                quantity.requestFocus();
                Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("min_qty_alert")
                        + " (" + saleLine.getProduct().getNumber() + ")");
            }
        });
    }

    private int pickProductLimitQty() {
        Properties options = RhoeConfig.get();
        String min = options.getProperty("min_on_sale");
        return min.equals("1") ? Integer.parseInt(min) : saleLine.getProduct().getMinimumQuantity();
    }

}

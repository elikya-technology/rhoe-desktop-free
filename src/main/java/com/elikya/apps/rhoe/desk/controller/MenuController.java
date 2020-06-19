/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.elikya.apps.rhoe.desk.observers.impl.DecimalsObserverImpl;
import com.elikya.apps.rhoe.desk.observers.impl.CRUDMasterImpl;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
public class MenuController implements Initializable {

    @FXML private ImageView picture;
    @FXML private JFXButton products;
    @FXML private JFXButton about;
    @FXML private JFXButton sales;
    @FXML private JFXButton options;
    @FXML private Label enterpriseName;
    @FXML private Label enterpriseSlogan;

    private static Label title;
    private Properties preferences;
    private Properties lang;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preferences = RhoeConfig.get();
        lang = ControlsHandler.getLanguage();
        setLanguage();
        ControlsHandler.handlePicture(picture);
        setProductsEventHandler();
        setSellsEventHandler();
        setAboutEventHandler();
        setEnterpriseInfos();
        setOptionsEventHandler();
    }

    public static void setTitle(Label _title) {
        title = _title;
    }

    private void setProductsEventHandler() {
        products.setOnAction(event -> {
            ControlsHandler.closeDrawer();
            Platform.runLater(() -> {
                unregisterObservers();
                title.setText(lang.getProperty("products"));
                MainController.setText("products");
                Stages.showLargeStage(StagesPaths.PRODUCTS);
            });
        });
    }

    private void setSellsEventHandler() {
        sales.setOnAction(event -> {
            ControlsHandler.closeDrawer();
            Platform.runLater(() -> {
                unregisterObservers();
                title.setText(lang.getProperty("sales"));
                MainController.setText("sales");
                Stages.showLargeStage(StagesPaths.SALES);
            });
        });
    }

    private void unregisterObservers() {
        CRUDMasterImpl.unregisterAll();
        DecimalsObserverImpl.unregister();
    }

    private void setOptionsEventHandler() {
        options.setOnAction(event -> {
            ControlsHandler.closeDrawer();
            Platform.runLater(() -> Stages.showDialog(StagesPaths.OPTIONS));
        });
    }

    private void setAboutEventHandler() {
        about.setOnAction(event -> {
            ControlsHandler.closeDrawer();
            Platform.runLater(() -> Stages.showDialog(StagesPaths.ABOUT));
        });
    }

    private void setEnterpriseInfos() {
        enterpriseName.setText(preferences.getProperty("enterprise"));
        enterpriseSlogan.setText(preferences.getProperty("business_words"));
    }

    private void setLanguage() {
        sales.setText("\t" + lang.getProperty("sales"));
        products.setText("\t" + lang.getProperty("products"));
        options.setText("\t" + lang.getProperty("options"));
        about.setText("\t" + lang.getProperty("about"));
    }
}

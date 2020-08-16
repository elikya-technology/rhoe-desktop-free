/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.elikya.apps.rhoe.desk.host.Computer;
import com.elikya.apps.rhoe.desk.host.Store;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
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
public class EnterpriseController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private JFXTextField name;
    @FXML private JFXTextField slogan;
    @FXML private JFXButton save;

    private Properties lang;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setLanguage();
        setNameProperty();
        setCloseEventHandler();
        setSaveEventHandler();
    }

    private void setLanguage() {
        lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty("your_business"));
        name.setPromptText(lang.getProperty("enterprise_name"));
        slogan.setPromptText(lang.getProperty("business_few_words"));
        save.setText(lang.getProperty("next"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setNameProperty() {
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) save.setDisable(true);
            else save.setDisable(false);
        });
    }

    private void setSaveEventHandler() {
        save.setOnAction(event -> {
            Store store = Store.builder().about(slogan.getText()).name(name.getText())
                    .computer(Computer.getInstance()).build();
            writeEnterpriseInfo(store);

            Stages.close(event);
            Stages.showNextStage();
        });
    }

    private void writeEnterpriseInfo(Store store) {
        Properties config = RhoeConfig.get();
        config.replace("enterprise", store.getName());
        config.replace("business_words", store.getAbout());
        RhoeConfig.write(config);

    }

}

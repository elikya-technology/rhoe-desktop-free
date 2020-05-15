/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setLanguage();
        setNameProperty();
        setCloseEventHandler();
        setSaveEventHandler();
    }

    private void setLanguage() {
        Properties lang = ControlsHandler.getLanguage();
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
            Stages.close(event);
            Properties config = RhoeConfig.get();
            config.replace("enterprise", name.getText());
            if (!slogan.getText().isEmpty()) {
                config.replace("business_words", slogan.getText());
            }
            RhoeConfig.write(config);
            Stages.showNextStage();
        });
    }
}

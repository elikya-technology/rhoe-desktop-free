/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.util.Configs;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.springframework.stereotype.Component;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class LanguageController implements Initializable {

    @FXML
    private JFXButton close;
    @FXML
    private JFXComboBox<String> language;
    @FXML
    private JFXButton save;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setLanguageItems();
        setCloseEventHandler();
        setSaveEventHandler();
    }

    private void setLanguageItems() {
        language.getItems().setAll("English", "French");
        language.getSelectionModel().selectFirst();
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setSaveEventHandler() {
        save.setOnAction(event -> {
            String item = language.getSelectionModel().getSelectedItem();
            Properties properties = Configs.get();
            properties.replace("language", item);
            Configs.write(properties);
            Stages.close(event);
            Stages.showNextStage();
        });
    }
}

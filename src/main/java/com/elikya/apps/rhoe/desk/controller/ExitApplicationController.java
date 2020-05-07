/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.util.Configs;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

@Component
public class ExitApplicationController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private Label text;
    @FXML private JFXButton no;
    @FXML private JFXButton yes;
    @FXML private JFXCheckBox doNotAskAgain;

    private Properties lang;
    private Properties configs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lang = ControlsHandler.getLanguage();
        configs = Configs.get();
        setLanguage();
        setCloseEventHandler();
        setNoEventHandler();
        setYesEventHandler();
        setDoNotAskAgainEventHandler();
    }

    private void setLanguage() {
        doNotAskAgain.setText(lang.getProperty("do_not_ask_again"));
        title.setText(lang.getProperty("exit_title"));
        text.setText(lang.getProperty("exit_question"));
        yes.setText(lang.getProperty("yes"));
        no.setText(lang.getProperty("no"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setNoEventHandler() {
        no.setOnAction(Stages::close);
    }

    private void setYesEventHandler() {
        yes.setOnAction(event -> {
            Configs.write(configs);
            Platform.exit();
        });
    }

    private void setDoNotAskAgainEventHandler() {
        doNotAskAgain.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                configs.put("enclosing_layout", "false");
            } else {
                configs.put("enclosing_layout", "true");
            }
        });
    }
}

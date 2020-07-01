/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

@Component
public class StarterOptionsController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private Label text;
    @FXML private JFXButton next;
    @FXML private JFXButton previous;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLanguage();
        setCloseEventHandler();
        setNextEventHandler();
        setPreviousEventHandler();
    }

    private void setLanguage() {
        Properties lang = ControlsHandler.getLanguage();
        text.setText(lang.getProperty("starter_options"));
        next.setText(lang.getProperty("close"));
        previous.setText(lang.getProperty("previous"));
        title.setText(lang.getProperty("options"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setNextEventHandler() {
        disableGetStarted();
        next.setOnAction(Stages::close);
    }

    private void disableGetStarted() {
        Properties configs = RhoeConfig.get();
        boolean showStarters = Boolean.parseBoolean(configs.getProperty("show_starter"));
        if (showStarters) {
            configs.replace("show_starter", "false");
            RhoeConfig.write(configs);
        }
    }

    private void setPreviousEventHandler() {
        previous.setOnAction(event -> {
            Stages.close(event);
            Stages.showDialog(StagesPaths.STARTER_CHART);
        });
    }
}

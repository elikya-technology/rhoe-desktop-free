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
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

@Component
public class WelcomeController implements Initializable {

    public static final int CIRCLE_VALUES = 140;
    @FXML private Label title;
    @FXML private Label text;
    @FXML private JFXButton close;
    @FXML private ImageView image;
    @FXML private JFXButton getStarted;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RhoeConfig.load(); // TO BE DISABLED !!!
        setLanguage();
        setCloseEventHandler();
        setGetStartedEventHandler();
    }

    private void setLanguage() {
        Properties lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty("welcome"));
        getStarted.setText(lang.getProperty("get_started"));
        text.setText(lang.getProperty("get_started_text"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setGetStartedEventHandler() {
        getStarted.setOnAction(event -> {
            Stages.close(event);
            Stages.showDialog(StagesPaths.STARTER_MORE);
        });
    }
}

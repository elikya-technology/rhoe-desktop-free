/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class WelcomeController implements Initializable {

    public static final int CIRCLE_VALUES = 140;
    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private ImageView image;
    @FXML private JFXButton getStarted;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RhoeConfig.load();
        ControlsHandler.circleImage(image, CIRCLE_VALUES, CIRCLE_VALUES, CIRCLE_VALUES);
        setCloseEventHandler();
        setGetStartedEventHandler();
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setGetStartedEventHandler() {
        getStarted.setOnAction(event -> {

        });
    }
}

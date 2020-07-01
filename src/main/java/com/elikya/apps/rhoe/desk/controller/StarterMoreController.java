/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

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
public class StarterMoreController implements Initializable {

        @FXML private Label title;
        @FXML private JFXButton close;
        @FXML private Label text;
        @FXML private JFXButton next;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            setLanguage();
            setCloseEventHandler();
            setNextEventHandler();
        }

        private void setLanguage() {
            Properties lang = ControlsHandler.getLanguage();
            title.setText(lang.getProperty("more"));
            text.setText(lang.getProperty("starter_more"));
            next.setText(lang.getProperty("next"));
        }

        private void setCloseEventHandler() {
            close.setOnAction(Stages::close);
        }

        private void setNextEventHandler() {
            next.setOnAction(event -> {
                Stages.close(event);
                Stages.showDialog(StagesPaths.STARTER_PRODUCTS);
            });
        }
}

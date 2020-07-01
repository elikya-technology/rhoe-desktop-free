/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class StarterMoreController implements Initializable {

        @FXML private Label title;
        @FXML private JFXButton close;
        @FXML private Label text;
        @FXML private JFXButton next;
        @FXML private JFXButton previous;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            setCloseEventHandler();
            setNextEventHandler();
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

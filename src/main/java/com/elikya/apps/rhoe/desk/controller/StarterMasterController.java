/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.ui.Stages;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class StarterMasterController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private AnchorPane container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCloseEventHandler();
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }
}

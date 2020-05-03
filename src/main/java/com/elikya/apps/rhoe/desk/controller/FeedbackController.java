/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

@Component
public class FeedbackController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private JFXTextField subject;
    @FXML private JFXTextArea content;
    @FXML private JFXButton submit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLanguage();
        setCloseEventHandler();
        setSubjectTextProperty();
    }

    private void setLanguage() {
        Properties lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty("feedback"));
        subject.setPromptText(lang.getProperty("subject"));
        content.setPromptText(lang.getProperty("content"));
        submit.setText(lang.getProperty("submit"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setSubjectTextProperty() {
        subject.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty())
                submit.setDisable(true);
            else submit.setDisable(false);
        });
    }

}

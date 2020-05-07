/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.host.BackendService;
import com.elikya.apps.rhoe.desk.host.Feedback;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.Configs;
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

    Properties lang;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLanguage();
        setCloseEventHandler();
        setSubmitEventHandler();
        setSubjectTextProperty();
    }

    private void setLanguage() {
        lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty("feedback"));
        subject.setPromptText(lang.getProperty("subject"));
        content.setPromptText(lang.getProperty("content"));
        submit.setText(lang.getProperty("submit"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setSubmitEventHandler() {
        submit.setOnAction(event -> {
            String email = Configs.get().getProperty("mail_address");
            Feedback feedback = Feedback.builder().content(content.getText())
                    .email(email).subject(subject.getText()).name("desktop-user").build();
            boolean submitted = BackendService.requestSubmitFeedback(feedback);
            if (submitted) {
                Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty("feedback_submitted"));
                Stages.close(event);
            }
        });
    }

    private void setSubjectTextProperty() {
        subject.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty())
                submit.setDisable(true);
            else submit.setDisable(false);
        });
    }

}

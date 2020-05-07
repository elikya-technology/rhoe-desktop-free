/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.observers.impl.ValidationObserverImpl;
import com.elikya.apps.rhoe.desk.observers.interfaces.ValidationObserver;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.Configs;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class LoginController implements Initializable, ValidationObserver {

    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXButton close;
    @FXML
    private JFXButton submit;
    @FXML
    private Label login;
    @FXML
    private Hyperlink forgottenPassword;

    private String appPassword;
    private Properties lang;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        appPassword = Configs.get().getProperty("password");
        ValidationObserverImpl.register(this);
        lang = ControlsHandler.getLanguage();
        setLanguage();
        setCloseEventHandler();
        setSubmitEventHandler();
        setPasswordEventHandler();
        setForgottenPasswordEventHandler();
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setLanguage() {
        login.setText(lang.getProperty("log_in"));
        password.setPromptText(lang.getProperty("password"));
        submit.setText(lang.getProperty("next"));
        forgottenPassword.setText(lang.getProperty("password_forgotten"));
    }

    private void setSubmitEventHandler() {
        submit.setOnAction(event -> {
            if (password.getText().equals(appPassword)) {
                closeCurrentLayout();
            } else {
                password.requestFocus();
                Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("wrong_password"));
            }
        });
    }

    private void closeCurrentLayout() {
        Stage stage = (Stage) submit.getScene().getWindow();
        stage.close();
        Stages.showMainApplication();
    }

    private void setPasswordEventHandler() {
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) submit.setDisable(true);
            else submit.setDisable(false);
        });
    }

    private void setForgottenPasswordEventHandler() {
        forgottenPassword.setOnAction(event -> {
            Stages.close(event);
            CodeVerifierController.setContext(CodeVerifierController.VerificationContext.UPDATING);
            Stages.showDialog(StagesPaths.CODE_VERIFIER);
        });
    }

    @Override
    public void processUpdateValidation() {
        closeCurrentLayout();
    }
}

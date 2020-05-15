/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.host.BackendHostHandler;
import com.elikya.apps.rhoe.desk.host.BackendService;
import com.elikya.apps.rhoe.desk.host.Subscriber;
import com.elikya.apps.rhoe.desk.observers.impl.ValidationObserverImpl;
import com.elikya.apps.rhoe.desk.observers.interfaces.ValidationObserver;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.elikya.apps.rhoe.desk.util.InputRegex;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class AccountController implements Initializable, ValidationObserver {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private JFXTextField mailAddress;
    @FXML private JFXButton next;

    private Properties lang;
    private Subscriber subscriber;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ValidationObserverImpl.register(this);
        setLanguage();
        setCloseEventHandler();
        setMailAddressTextProperty();
        setNextEventHandler();
    }

    private void setLanguage() {
        lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty("your_account"));
        mailAddress.setPromptText(lang.getProperty("mail_address"));
        next.setText(lang.getProperty("next"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setMailAddressTextProperty() {
        mailAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) next.setDisable(true);
            else next.setDisable(false);
        });
    }

    private void setNextEventHandler() {
        next.setOnAction(event -> Platform.runLater(() -> {
            if (emailIsCorrect()) {
                if (BackendHostHandler.isConnected()) {
                    processVerificationSteps(event);
                }
            }
            else
                Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_email"));
        }));
    }

    private boolean emailIsCorrect() {
        return mailAddress.getText().matches(InputRegex.EMAIL.regex);
    }

    private void processVerificationSteps(ActionEvent event) {
        Optional<Boolean> result = BackendService.emailExists(mailAddress.getText());
        result.ifPresent(value -> {
            if (value) Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("mail_taken"));
            else callCodeVerifier(event);
        });
    }

    private void callCodeVerifier(ActionEvent event) {
        Stages.close(event);
        CodeVerifierController.setContext(CodeVerifierController.VerificationContext.UPDATING);
        CodeVerifierController.setEmail(mailAddress.getText());
        Stages.showDialog(StagesPaths.CODE_VERIFIER);
    }

    @Override
    public void processUpdateValidation() {
        Optional<Subscriber> result = BackendService.requestSaveAccount(buildSubscriber());
        result.ifPresent(value -> {
            subscriber = result.get();
            if (!subscriber.isEmpty()) {
                writeAccountData();
                ValidationObserverImpl.unregister(this);
                Stages.showNextStage();
            }
        });
    }

    private Subscriber buildSubscriber() {
        return Subscriber.builder().email(mailAddress.getText()).build();
    }

    private void writeAccountData() {
        Properties configs = RhoeConfig.get();
        configs.put("mail_address", subscriber.getEmail());
        configs.put("subs_key", subscriber.getId());
        RhoeConfig.write(configs);
    }

}

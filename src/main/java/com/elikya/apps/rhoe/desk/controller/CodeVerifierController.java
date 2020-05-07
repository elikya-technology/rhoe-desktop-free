/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.host.BackendService;
import com.elikya.apps.rhoe.desk.observers.impl.ValidationObserverImpl;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.Configs;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;

/**
 *
 * @author Mafole Loemelah
 */

@Component
public class CodeVerifierController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private JFXTextField code;
    @FXML private JFXButton submit;
    @FXML private Label time;

    public static final int REST_TIME = 90;
    private static VerificationContext context;
    private static String email;

    private Properties lang;
    private int counterDown;
    private Timer timer;
    private String codeText;


    public static void setEmail(String theEmail) {
        email = theEmail;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLanguage();
        callEmailSender();
        setSubmitEventHandler();
        setCloseEventHandler();
        setCodeTextProperty();
        initTimer();
        Notifier.notify(StagesPaths.INFO_NOTIF, lang.getProperty("check_email"));
    }

    public static void setContext(VerificationContext _context) {
        context = _context;
    }

    private void setLanguage() {
        lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty("code_validation"));
        code.setPromptText(lang.getProperty("enter_code"));
        submit.setText(lang.getProperty("submit"));
    }

    private void setSubmitEventHandler() {
        submit.setOnAction(e -> {
            if (!codeText.isEmpty()) {
                if (codeText.equals(code.getText())) {
                    timer.cancel();
                    callObserver();
                    Stages.close(e);
                } else {
                    Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_code"));
                    code.requestFocus();
                    code.selectAll();
                }
            }
        });
    }

    private void callObserver() {
        if (context.equals(VerificationContext.DELETION))
            ValidationObserverImpl.executeProcessDelete();
        if (context.equals(VerificationContext.UPDATING))
            ValidationObserverImpl.executeProcessUpdate();
        if (context.equals(VerificationContext.STOCK_UP))
            ValidationObserverImpl.executeProcessStockingUp();
        if (context.equals(VerificationContext.WITHDRAW))
            ValidationObserverImpl.executeProcessWithdraw();

        nullifyFields();
    }

    private void nullifyFields() {
        email = null;
        codeText = "";
    }

    private void setCloseEventHandler() {
        close.setOnAction(event -> {
            timer.cancel();
            email = "";
            Stages.close(event);
        });
    }

    private void setCodeTextProperty() {
        code.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) submit.setDisable(true);
            else submit.setDisable(false);
        });
    }

    private void callEmailSender() {
        Platform.runLater(() -> {
            if (email == null)
                email = getConfigsEmail();
            Optional<String> codeValue = BackendService.requestSendMail(email);
            codeText = codeValue.orElse("");
        });
    }

    private String getConfigsEmail() {
        return Configs.get().getProperty("mail_address");
    }

    private void initTimer() {
        counterDown = REST_TIME;
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                counterDown--;
                Platform.runLater(() -> time.setText("00:" + counterDown));
                if (counterDown == 0) {
                    timer.cancel();
                    email = null;
                    Platform.runLater(() -> {
                        Stage stage = (Stage) close.getScene().getWindow();
                        stage.close();
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    public enum VerificationContext {DELETION, UPDATING, STOCK_UP, WITHDRAW}

}

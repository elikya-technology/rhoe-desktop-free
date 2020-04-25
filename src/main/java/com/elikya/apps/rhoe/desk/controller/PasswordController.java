/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class PasswordController implements Initializable {

    @FXML
    private Label title;
    @FXML
    private JFXButton close;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXButton save;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setLanguage();
        setCloseEventHandler();
        setPasswordEventHandler();
    }

    private void setLanguage() {
        Properties language = ControlsHandler.getLanguage();
        title.setText(language.getProperty("password"));
        password.setPromptText(language.getProperty("password_prompt"));
        save.setText(language.getProperty("verify"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setPasswordEventHandler() {
        password.textProperty().addListener((observable,
                oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                save.setDisable(true);
            } else {
                save.setDisable(false);
            }
        });
    }

    private void setSaveEventHandler() {
        save.setOnAction(event -> {
//            Properties config = PropertiesUtil.read(PropertiesUtil.Target.OPTIONS);
//            Map<String, String> account = new HashMap<>(1);
//            account.put("username", config.getProperty("account"));
//            account.put("password", password.getText());
//            boolean result = BackendHost.verifyAccount(account);
//            if (result) {
//
//            } else {
//                System.out.println("INVALID PASSWORD");
//            }
        });
    }

}

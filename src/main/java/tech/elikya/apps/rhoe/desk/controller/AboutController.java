/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.controller;

import tech.elikya.apps.rhoe.desk.ui.ControlsHandler;
import tech.elikya.apps.rhoe.desk.ui.Stages;
import tech.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
public class AboutController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private Label text;
    @FXML private Label version;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setLanguage();
        setCloseEventHandler();
        setVersion();
    }

    private void setLanguage() {
        Properties lang = ControlsHandler.getLanguage();
        text.setText(lang.getProperty("about_text"));
        title.setText(lang.getProperty("about"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }
    
    private void setVersion() {
        version.setText(RhoeConfig.get().getProperty("version"));
    }
}
